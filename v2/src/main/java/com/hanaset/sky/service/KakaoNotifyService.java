package com.hanaset.sky.service;

import com.hanaset.sky.cache.KakaoCache;
import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import com.hanaset.sky.entitiy.SkyParamEntity;
import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.KakaoSQSItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Service
public class KakaoNotifyService {

    @Autowired
    private KakaoCache kakaoCache;

    @Autowired
    private SQSClient sqsClient;

    @Autowired
    private SkyMsgLogRepository logRepository;


    public ResponseEntity<ResponseItem> sendMessage(RequestMsg request) {

        SkyKakaoTemplateEntity entity = kakaoCache.findForTemplate(request.getCode());

        long reqTime = System.currentTimeMillis();

        String editMsg;
        ResponseItem result = verifyRequest(request, entity);

        if (result == null) {

            editMsg = getMessage(request.getParam(), entity);

            KakaoSQSItem item = KakaoSQSItem.builder()
                    .medium("kakao-sms")
                    .recipient(request.getTo())
                    .template(request.getCode())
                    .text(editMsg)
                    .build();

            sqsClient.send(item);

            result = ResponseItem.builder()
                    .code("0")
                    .data(new JSONObject())
                    .build();
        }

        SkyMsgLogEntity logEntity = SkyMsgLogEntity.builder()
                .code(request.getCode())
                .address(request.getTo())
                .msgType("kakao-sms")
                .param(request.getParam().toString())
                .reqTime(reqTime)
                .compTime(System.currentTimeMillis())
                .result(result.getCode())
                .build();

        logRepository.save(logEntity);

        return new ResponseEntity<ResponseItem>(result, getHttpStatus(result.getCode()));
    }


    private String getMessage(JSONObject jsonObject, SkyKakaoTemplateEntity skyKakaoTemplateEntity) {

        String message = skyKakaoTemplateEntity.getContext();

        if(jsonObject != null) {
            Set key = jsonObject.keySet();
            Iterator<String> iterator = key.iterator();

            while (iterator.hasNext()) {

                String keyName = iterator.next();
                List<String> params = paramConvert(keyName);

                for (String param : params) {
                    String replaceKey = "#\\{" + param + "\\}";
                    try {
                        message = message.replaceAll(replaceKey, jsonObject.get(keyName).toString());
                    } catch (PatternSyntaxException e) {
                        log.error("Kakao replace Key Error");
                    }
                }
            }
        }
        return message;
    }

    private List<String> paramConvert(String param) {

        List<String> params = new ArrayList<>();

        if (param.equals("user_name")) {
            params.add("회원이름");
        } else if (param.equals("amount")) {
            params.add("수량");
        } else if (param.equals("email")) {
            params.add("이메일");
        } else if (param.equals("datetime")) {
            params.add("일시");
        } else if (param.equals("ip")) {
            params.add("IP 주소");
        } else if (param.equals("os")) {
            params.add("OS");
        } else if (param.equals("browser")) {
            params.add("Browser");
        } else if (param.equals("deposit_code")) {
            params.add("입금코드");
        } else if (param.equals("corporate_account")) {
            params.add("법인계좌정보");
        } else if (param.equals("auth_account")) {
            params.add("점유인증계좌");
        } else if (param.equals("bank_code")) {
            params.add("은행별 코드기입란 명칭");
        } else if (param.equals("expiration_time")) {
            params.add("만료시간");
        } else if (param.equals("coin")) {
            params.add("코인");
        } else if (param.equals("certification_number")) {
            params.add("인증번호");
        } else if (param.equals("edit_date")) {
            params.add("변경시간");
        } else if (param.equals("collateral_ratio")) {
            params.add("담보비율");
        } else if (param.equals("termination_type")) {
            params.add("종료유형");
        } else if (param.equals("valuation_loss")) {
            params.add("평가손익률");
        } else if (param.equals("guarantee")) {
            params.add("담보물");
        } else if (param.equals("rental")) {
            params.add("대여물");
        } else if (param.equals("fee")) {
            params.add("수수료정보");
        } else {
            params.add(param); // param가 영어 인 경우
        }

        return params;
    }


    public ResponseItem verifyRequest(RequestMsg request, SkyKakaoTemplateEntity entity) {

        JSONObject result = new JSONObject();

        if (entity == null) { // Code isn't exist
            log.error("Code is not exist Code -> {}", request.getCode());
            //result.put("result", "failed");
            result.put("error", "Code is not exist");
            return ResponseItem.builder()
                    .code("__no_request_param")
                    .data(result)
                    .build();
        }

        if (!vaildateNumber(request.getTo())) { // The phone number format is not correct
            log.error("Phone number don't match format PhoneNumber -> {}", request.getTo());
            //result.put("result", "failed");
            result.put("error", "Phone number is not format");
            return ResponseItem.builder()
                    .code("__no_request_param")
                    .data(result)
                    .build();
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.error("Parameter Not match data -> {}", request.getParam());
            //result.put("result", "failed");
            result.put("error", paramResult);
            return ResponseItem.builder()
                    .code("__no_request_param")
                    .data(result)
                    .build();
        }

        return null;
    }


    private boolean vaildateNumber(String number) {

        String regex_phone_no = "^\\+?[1-9]\\d{1,14}$";

        return number.matches(regex_phone_no);
    }

    private boolean verifyParamValue(String key, String value) {

        if (key.contains("date")) {
            if (!vaildateDate(value)) {
                return false;
            }
        }

        return true;
    }

    private boolean vaildateDate(String datetime) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            Date date = format.parse(datetime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String verifyParam(SkyKakaoTemplateEntity entity, JSONObject object) {

        SkyParamEntity paramEntity = entity.getParam();

        if (paramEntity.getParams() != null) {

            String params = paramEntity.getParams();

            String[] array_param = params.split(",");

            if (array_param.length != object.size()) {
                return "Number of parameters is incorrect";
            }

            for (String param : array_param) {

                try {
                    String str = object.get(param).toString();

                    if (!verifyParamValue(param, str)) {
                        return param + "'s value is error";
                    }

                } catch (NullPointerException e) {
                    return param + " is Error";
                }
            }
        } else {

            if(object.size() != 0)
                return "Number of parameters is incorrect";
        }
        return null;
    }

    private HttpStatus getHttpStatus(String code){

        HttpStatus status;

        if(code.equals("0")){
            status = HttpStatus.OK;
        }else if(code.equals("__no_req_param")){
            status = HttpStatus.BAD_REQUEST;
        }else{
            status = HttpStatus.NOT_FOUND;
        }

        return status;
    }


}

