package com.hanaset.sky.service;

import com.hanaset.sky.cache.EmailCache;
import com.hanaset.sky.entity.SkyEmailTemplateEntity;
import com.hanaset.sky.entity.SkyMsgLogEntity;
import com.hanaset.sky.entity.SkyParamEntity;
import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.EmailSQSItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.PatternSyntaxException;


@Slf4j
@Service
public class EmailNotifyService {

    @Autowired
    SQSClient sqsClient;

    @Autowired
    EmailCache emailCache;

    @Autowired
    SkyMsgLogRepository logRepository;

    private String getMessage(JSONObject jsonObject, SkyEmailTemplateEntity entity) {

        ClassPathResource cpr = new ClassPathResource("/templates/" + entity.getFileName());

        try {

            byte[] bytes = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            String message = new String(bytes, StandardCharsets.UTF_8);

            if(jsonObject != null) {

                Set key = jsonObject.keySet();
                Iterator<String> iterator = key.iterator();

                while (iterator.hasNext()) {

                    String keyName = iterator.next();
                    List<String> params = paramConvert(keyName);

                    for (String param : params) {
                        String replaceKey = "__" + param + "__";
                        try {
                            message = message.replaceAll(replaceKey, jsonObject.get(keyName).toString());
                        } catch (PatternSyntaxException e) {
                            log.error("Email replace Key Error");
                        }
                    }
                }

            }

            return message;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public ResponseEntity<ResponseItem> sendMessage(RequestMsg request) {

        SkyEmailTemplateEntity entity = emailCache.findForTemplate(request.getCode());

        long reqTime = System.currentTimeMillis();

        String editMsg;
        ResponseItem result = verifyRequest(request, entity);
        if (result == null) {

            editMsg = getMessage(request.getParam(), entity);

            EmailSQSItem item = EmailSQSItem.builder()
                    .medium("email")
                    .recipients(request.getTo())
                    .subject(entity.getSubject())
                    .html(editMsg)
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
                .msgType("email")
                .param(request.getParam().toString())
                .reqTime(reqTime)
                .compTime(System.currentTimeMillis())
                .result(result.getCode())
                .build();

        logRepository.save(logEntity);

        return new ResponseEntity<ResponseItem>(result, getHttpStatus(result.getCode()));
    }

    private List<String> paramConvert(String param) {

        List<String> params = new ArrayList<>();

        if (param.equals("ip")) {
            params.add("IP");
        } else if (param.equals("user_name")) {
            params.add("USER_NAME");
        } else if (param.equals("datetime")) {
            params.add("TIMESTAMP");
        } else if (param.equals("os")) {
            params.add("OS");
        } else if (param.equals("browser")) {
            params.add("BROWSER");
        } else if (param.equals("verification_code")) {
            params.add("VERIFICATION_CODE");
        } else if (param.equals("password_reset_link")) {
            params.add("PASSWORD_RESET_LINK");
        } else if (param.equals("email_address")) {
            params.add("EMAIL_ADDRESS");
        } else if (param.equals("verification_link")) {
            params.add("VERIFICATION_LINK");
        }

        params.add(param);


        return params;
    }

    public ResponseItem verifyRequest(RequestMsg request, SkyEmailTemplateEntity entity) {

        JSONObject result = new JSONObject();

        if (entity == null) { // Code isn't exist
            log.error("Code isn't exist Code -> {}", request.getCode());
            //result.put("result", "failed");
            result.put("error", "Code is not exist");
            return ResponseItem.builder()
                    .code("__no_request_code_")
                    .data(result)
                    .build();
        }

        if (!vaildateEamil(request.getTo())) { // Email format is not correct
            log.error("Email Address don't match format Email Address -> {}", request.getTo());
            //result.put("result", "failed");
            result.put("error", "Email address is not format");
            return ResponseItem.builder()
                    .code("__no_request_code_")
                    .data(result)
                    .build();
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.error("Parameter Not match date -> {}", request.getParam());
            //result.put("result", "failed");
            result.put("error", paramResult);
            return ResponseItem.builder()
                    .code("__no_request_code_")
                    .data(result)
                    .build();
        }

        return null;
    }

    private boolean vaildateEamil(String email) {

        String regex_email = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        return email.matches(regex_email);
    }

    private String verifyParam(SkyEmailTemplateEntity entity, JSONObject object) {

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
