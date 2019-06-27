package com.hanaset.sky.sender;
import com.hanaset.sky.MsgPool.MsgItemEmail;
import com.hanaset.sky.MsgPool.MsgPoolEmail;
import com.hanaset.sky.config.EmailConfig;
import com.hanaset.sky.entitiy.SkyMsgLogEntity;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Random;

@Component
public class EmailSender {

    @Autowired
    private MsgPoolEmail msgPool;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private SkyMsgLogRepository skyMsgLogRepository;

    @Async
    public synchronized void emailSend(){

        Random random = new Random();

        while(true){

            if(msgPool.size() != 0){

                MsgItemEmail item = msgPool.pop_front();

                BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(emailConfig.getAccessKeyId(), emailConfig.getSecretAccessKey());

                AmazonSimpleEmailServiceAsync client = AmazonSimpleEmailServiceAsyncClient.asyncBuilder()
                        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                        .withRegion(emailConfig.getRegion())
                        .build();

                client.sendEmailAsync(toSendRequest(item));

                System.out.println("email send!!");

                SkyMsgLogEntity entity = new SkyMsgLogEntity();
                entity.setId(String.format("%06d", random.nextInt(10000000)) + Long.toString(System.currentTimeMillis()));
                entity.setMsgType("email");
                entity.setCode(item.getEmailTemplateEntity().getId());
                entity.setTime(new Timestamp(System.currentTimeMillis()));
                entity.setAddress(item.getTo());
                //entity.setResult("success");

                skyMsgLogRepository.save(entity);

            }else{

                try{
                    System.out.println("email_msg_pool empty");
                    Thread.sleep(1000);
                    continue;
                }catch (InterruptedException e) {
                    //e.printStackTrace();
                }

            }
        }

    }

    private SendEmailRequest toSendRequest(MsgItemEmail item){

        Destination destination = new Destination().withToAddresses(item.getTo());

        Message message = new Message().withSubject(createContent(item.getEmailTemplateEntity().getSubject()))
                .withBody(new Body().withHtml(createContent(item.getMsg())));

        return new SendEmailRequest().withSource(emailConfig.getFrom()).withDestination(destination)
                .withMessage(message);
    }

    private Content createContent(String text){
        return new Content().withCharset("UTF-8").withData(text);
    }
}
