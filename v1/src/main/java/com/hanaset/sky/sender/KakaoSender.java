package com.hanaset.sky.sender;

import com.hanaset.sky.MsgPool.MsgItemKakao;
import com.hanaset.sky.MsgPool.MsgPoolKakao;
import com.hanaset.sky.config.KakaoConfig;
import com.hanaset.sky.entitiy.SkyKakaoButtonEntity;
import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Random;

@Component
public class KakaoSender {

    private HttpComponentsClientHttpRequestFactory factory;
    private HttpClient httpClient;
    private RestTemplate restTemplate;

    @Autowired
    SkyMsgLogRepository skyMsgLogRepository;

    @Autowired
    MsgPoolKakao msgPoolKakao;

    @Autowired
    KakaoConfig kakaoConfig;

    public KakaoSender() {
        factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectionRequestTimeout(3000);
        httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(5)
                .build();
        factory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(factory);
    }

    @Async
    public synchronized void kakaoSend() {

        while (true) {

            if (msgPoolKakao.size() != 0) {

                MsgItemKakao item = msgPoolKakao.pop_front();

                ObjectMapper objectMapper = new ObjectMapper();
                Random random = new Random();

                JSONObject param = new JSONObject();

                param.put("msgid", String.format("%06d", random.nextInt(10000000)) + Long.toString(System.currentTimeMillis()));
                param.put("message_type", "at");
                param.put("profile_key", kakaoConfig.getKey());
                param.put("template_code", item.getEntity().getId());
                param.put("receiver_num", item.getNum());
                param.put("message", item.getMsg());
                param.put("reserved_time", "00000000000000");

                param.put("button1", getButton(item.getEntity().getButton()));

                SkyMsgLogEntity logEntity = new SkyMsgLogEntity();
                logEntity.setTime(new Timestamp(System.currentTimeMillis()));
                logEntity.setId(param.get("msgid").toString());
                logEntity.setCode(item.getEntity().getId());
                logEntity.setAddress(item.getNum());
                logEntity.setMsgType("kakao");
                logEntity.setResult("failed");

                String requestParam = null;
                try {

                    requestParam = objectMapper.writeValueAsString(param);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("userid", kakaoConfig.getId());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> request = new HttpEntity<>("[" + requestParam + "]", httpHeaders);
                    URI uri = UriComponentsBuilder.newInstance().scheme("https").host(kakaoConfig.getHost())
                            .path(kakaoConfig.getPath())
                            .build().expand(kakaoConfig.getKey()).encode().toUri();
                    String result = restTemplate.postForObject(uri, request, String.class);
                    logEntity.setResult(result);

                    skyMsgLogRepository.save(logEntity);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            } else {

                try {
                    System.out.println("kakao_msg_pool empty");
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public JSONObject getButton(SkyKakaoButtonEntity entity) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", entity.getName());
        jsonObject.put("url_pc", entity.getUrl_pc());
        jsonObject.put("url_mobile", entity.getUrl_mobile());
        jsonObject.put("type", entity.getType());

        return jsonObject;
    }
}
