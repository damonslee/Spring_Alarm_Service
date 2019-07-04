package com.hanaset.sky.service;

import com.hanaset.sky.cache.SmsCache;
import com.hanaset.sky.common.LogRecoder;
import com.hanaset.sky.common.SkyApiErrorCode;
import com.hanaset.sky.entity.SkyParamEntity;
import com.hanaset.sky.entity.SkySmsTemplateEntity;
import com.hanaset.sky.exception.SkyResponseException;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.SmsSQSItem;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Service
public class SmsNotifyService {

    @Autowired
    private SmsCache smsCache;

    @Autowired
    private SQSClient sqsClient;

    @Autowired
    private LogRecoder logRecoder;

    public void sendMessage(RequestMsg request) {

        SkySmsTemplateEntity entity = smsCache.findForTemplate(request.getCode());

        Timestamp reqTime = Timestamp.valueOf(LocalDateTime.now());

        String editmsg;

        if (request.getParam() == null) {
            request.setParam(new JSONObject());
        }

        verifyRequest(request, entity, reqTime);


        editmsg = getMessage(request.getParam(), entity);

        SmsSQSItem item = SmsSQSItem.builder()
                .medium("sms")
                .recipient(request.getTo())
                .text(editmsg)
                .build();

        sqsClient.send(item);

        logRecoder.recordLog(request, "success", reqTime);
    }

    private String getMessage(JSONObject jsonObject, SkySmsTemplateEntity skySmsTemplateEntity) {

        String message = skySmsTemplateEntity.getTemplate();

        if (jsonObject != null) {
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
        }
        params.add(param);

        return params;
    }

    public void verifyRequest(RequestMsg request, SkySmsTemplateEntity entity, Timestamp reqTime) {

        if (entity == null) { // Code isn't exist
            log.error("Code isn't exist Code -> {}", request.getCode());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Code is not exist");
        }

        if (!vaildateNumber(request.getTo())) { // The phone number format is not correct
            log.error("Phone Number don't match format PhoneNumber -> {}", request.getTo());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Phone number don't match format");
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.error("Parameter Not match data -> {}", request.getParam());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Parameter don't match data [" + paramResult + "]");
        }

    }

    private String verifyParam(SkySmsTemplateEntity entity, JSONObject object) {

        SkyParamEntity paramEntity = entity.getParam();

        if (paramEntity.getParams().equals("") && object != null) {

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

    private boolean vaildateNumber(String number) {

        String regex_phone_no = "(\\d{3,4})(\\d{3,4})(\\d{4})";

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
}
