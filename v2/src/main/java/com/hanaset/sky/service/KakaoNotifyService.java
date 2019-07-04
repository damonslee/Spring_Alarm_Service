package com.hanaset.sky.service;

import com.hanaset.sky.cache.KakaoCache;
import com.hanaset.sky.common.LogRecoder;
import com.hanaset.sky.common.SkyApiErrorCode;
import com.hanaset.sky.entity.SkyKakaoTemplateEntity;
import com.hanaset.sky.entity.SkyParamEntity;
import com.hanaset.sky.exception.SkyResponseException;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.sqs.SQSClient;
import com.hanaset.sky.item.KakaoSQSItem;
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
public class KakaoNotifyService {

    @Autowired
    private KakaoCache kakaoCache;

    @Autowired
    private SQSClient sqsClient;

    @Autowired
    private LogRecoder logRecoder;


    public void sendMessage(RequestMsg request) {

        SkyKakaoTemplateEntity entity = kakaoCache.findForTemplate(request.getCode());

        Timestamp reqTime = Timestamp.valueOf(LocalDateTime.now());

        String editMsg;

        verifyRequest(request, entity, reqTime);

        editMsg = getMessage(request.getParam(), entity);

        KakaoSQSItem item = KakaoSQSItem.builder()
                .medium("kakao-sms")
                .recipient(request.getTo())
                .template(request.getCode())
                .text(editMsg)
                .build();

        sqsClient.send(item);

        logRecoder.recordLog(request, "success", reqTime);
    }


    private String getMessage(JSONObject jsonObject, SkyKakaoTemplateEntity skyKakaoTemplateEntity) {

        String message = skyKakaoTemplateEntity.getContext();

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
        }

        params.add(param); // param가 영어 인 경우


        return params;
    }


    public void verifyRequest(RequestMsg request, SkyKakaoTemplateEntity entity, Timestamp reqTime) {

        if (entity == null) { // Code isn't exist
            log.warn("Code is not exist Code -> {}", request.getCode());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Code is not exist");
        }

        if (!vaildateNumber(request.getTo())) { // The phone number format is not correct
            log.warn("Phone number don't match format PhoneNumber -> {}", request.getTo());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Phone number don't match format");
        }

        String paramResult = verifyParam(entity, request.getParam());
        if (paramResult != null) { // required parameters are not exist && parameter value is invalidate
            log.warn("Parameter Not match data -> {}", request.getParam());
            logRecoder.recordLog(request, SkyApiErrorCode.INVALID_PARAMS, reqTime);
            throw new SkyResponseException(SkyApiErrorCode.INVALID_PARAMS, "Parameter don't match data [" + paramResult + "]");
        }
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = format.parse(datetime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String verifyParam(SkyKakaoTemplateEntity entity, JSONObject object) {

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
        } else if(paramEntity.getParams().equals("") || object == null){

                return "Number of parameters is incorrect";
        }
        return null;
    }
}

