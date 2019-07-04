package com.hanaset.sky.service;

import com.hanaset.sky.cache.EmailCache;
import com.hanaset.sky.common.LogRecoder;
import com.hanaset.sky.common.SkyApiErrorCode;
import com.hanaset.sky.entity.SkyEmailTemplateEntity;
import com.hanaset.sky.entity.SkyParamEntity;
import com.hanaset.sky.exception.SkyResponseException;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.EmailSQSItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.PatternSyntaxException;


@Slf4j
@Service
public class EmailNotifyService {

    @Autowired
    private SQSClient sqsClient;

    @Autowired
    private EmailCache emailCache;

    @Autowired
    private LogRecoder logRecoder;

    private String getMessage(JSONObject jsonObject, SkyEmailTemplateEntity entity) {

        ClassPathResource cpr = new ClassPathResource("/templates/" + entity.getFileName());

        try {

            byte[] bytes = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            String message = new String(bytes, StandardCharsets.UTF_8);

            if (jsonObject != null) {

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

    public void sendMessage(RequestMsg request) {

        SkyEmailTemplateEntity entity = emailCache.findForTemplate(request.getCode());

        Timestamp reqTime = Timestamp.valueOf(LocalDateTime.now());

        String editMsg;

        verifyRequest(request, entity, reqTime);

        editMsg = getMessage(request.getParam(), entity);

        EmailSQSItem item = EmailSQSItem.builder()
                .medium("email")
                .recipients(request.getTo())
                .subject(entity.getSubject())
                .html(editMsg)
                .build();

        sqsClient.send(item);

        logRecoder.recordLog(request, "success", reqTime);
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

    public void verifyRequest(RequestMsg request, SkyEmailTemplateEntity entity, Timestamp reqTime) {

        JSONObject result = new JSONObject();

        if (entity == null) { // Code isn't exist
            log.error("Code isn't exist Code -> {}", request.getCode());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Code is not exist");
        }

        if (!vaildateEamil(request.getTo())) { // Email format is not correct
            log.error("Email Address don't match format Email Address -> {}", request.getTo());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Phone number don't match format");
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.error("Parameter Not match date -> {}", request.getParam());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Parameter don't match data [" + paramResult + "]");
        }
    }

    private boolean vaildateEamil(String email) {

        String regex_email = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        return email.matches(regex_email);
    }

    private String verifyParam(SkyEmailTemplateEntity entity, JSONObject object) {

        SkyParamEntity paramEntity = entity.getParam();

        if (!paramEntity.getParams().equals("") && object != null) {

            String params = paramEntity.getParams();

            String[] array_param = params.split(",");

            if (array_param.length != object.size()) {
                return "parameter [" + params + "]";
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
        } else if (paramEntity.getParams().equals("") || object == null) {
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
}
