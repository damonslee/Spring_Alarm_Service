package com.hanaset.sky.service;

import com.hanaset.sky.cache.SmsCache;
import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import com.hanaset.sky.entitiy.SkyParamEntity;
import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.SmsSQSItem;
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
public class SmsNotifyService {

    @Autowired
    SmsCache smsCache;

    @Autowired
    SQSClient sqsClient;

    @Autowired
    SkyMsgLogRepository logRepository;


    public ResponseEntity<ResponseItem> sendMessage(RequestMsg request) {

        SkySmsTemplateEntity entity = smsCache.findForTemplate(request.getCode());

        long reqTime = System.currentTimeMillis();

        String editmsg;
        ResponseItem result = verifyRequest(request, entity);

        if (result == null) {

            editmsg = getMessage(request.getParam(), entity);

            SmsSQSItem item = SmsSQSItem.builder()
                    .medium("sms")
                    .recipient(request.getTo())
                    .text(editmsg)
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
                .msgType("sms")
                .param(request.getParam().toString())
                .reqTime(reqTime)
                .compTime(System.currentTimeMillis())
                .result(result.getCode())
                .build();

        logRepository.save(logEntity);

        return new ResponseEntity<ResponseItem>(result, getHttpStatus(result.getCode()));
    }

    private String getMessage(JSONObject jsonObject, SkySmsTemplateEntity skySmsTemplateEntity) {

        String message = skySmsTemplateEntity.getTemplate();

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
                        log.error("sms replace Key Error");
                    }
                }
            }
        }
        return message;
    }

    private List<String> paramConvert(String param) {

        List<String> params = new ArrayList<>();

        if (param.equals("code")) {
            params.add("Code");
        } else if (param.equals("ip")) {
            params.add("IP Address");
        } else if (param.equals("coin")) {
            params.add("asset");
            params.add("coin");
        } else if (param.equals("date")) {
            params.add("Time");
        } else {
            params.add(param);
        }

        return params;
    }

    public ResponseItem verifyRequest(RequestMsg request, SkySmsTemplateEntity entity) {

        JSONObject result = new JSONObject();

        if (entity == null) { // Code isn't exist
            log.error("Code isn't exist Code -> {}", request.getCode());
            //result.put("result", "failed");
            result.put("error", "Code is not exist");

            return ResponseItem.builder().code("__no_request_param_")
                    .data(result)
                    .build();
        }

        if (!vaildateNumber(request.getTo())) { // The phone number format is not correct
            log.error("Phone Number don't match format PhoneNumber -> {}", request.getTo());
            //result.put("result", "failed");
            result.put("error", "Phone number is not format");

            return ResponseItem.builder().code("__no_request_param_")
                    .data(result)
                    .build();
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.error("Parameter Not match data -> {}", request.getParam());
            //result.put("result", "failed");
            result.put("error", paramResult);

            return ResponseItem.builder().code("__no_request_param_")
                    .data(result)
                    .build();
        }

        return null;
    }

    private String verifyParam(SkySmsTemplateEntity entity, JSONObject object) {

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
