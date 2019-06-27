package com.hanaset.sky.sender;

import com.hanaset.sky.MsgPool.MsgItemSms;
import com.hanaset.sky.MsgPool.MsgPoolSms;
import com.hanaset.sky.config.SmsConfig;
import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Random;

@Component
public class SmsSender {

    @Autowired
    MsgPoolSms msgPool;

    @Autowired
    SkyMsgLogRepository skyMsgLogRepository;

    @Autowired
    SmsConfig smsConfig;


    @Async
    public synchronized void smsSend(){

        Random random = new Random();

        while(true){

            if(msgPool.size() != 0){

                MsgItemSms item = msgPool.pop_front();

                AmazonSNSClient client = new AmazonSNSClient(new BasicAWSCredentials(smsConfig.getAccessKeyId(), smsConfig.getSecretAccessKey()));
                String result = sendSMSmessage(client, item.getMsg(), item.getNum());

                SkyMsgLogEntity entity = new SkyMsgLogEntity();

                entity.setId(String.format("%06d", random.nextInt(10000000)) + Long.toString(System.currentTimeMillis()));
                entity.setMsgType("sms");
                entity.setCode(item.getEntity().getId());
                entity.setTime(new Timestamp(System.currentTimeMillis()));
                entity.setAddress(item.getNum());
                entity.setResult(result);

                skyMsgLogRepository.save(entity);

            }else{
                try{
                    System.out.println("sms_msg_pool empty");
                    Thread.sleep(1000);
                    continue;
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String sendSMSmessage(AmazonSNSClient client, String msg, String num){
        PublishResult result = client.publish(new PublishRequest().withMessage(msg).withPhoneNumber(num));
        return result.toString();
    }
}
