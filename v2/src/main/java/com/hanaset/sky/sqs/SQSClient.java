package com.hanaset.sky.sqs;

import com.hanaset.sky.config.SQSConfig;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class SQSClient {

    private AmazonSQS sqs;

    @Autowired
    private SQSConfig sqsConfig;

    @PostConstruct
    private void init() {

        log.info("aws SQS Client init start");

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(sqsConfig.getAccessKey(), sqsConfig.getSecretKey());

        System.out.println("==============================================");
        System.out.println("access : " + sqsConfig.getAccessKey());
        System.out.println("secret : " + sqsConfig.getSecretKey());
        System.out.println("region : " + sqsConfig.getRegion());

        try {
            sqs = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).withRegion(sqsConfig.getRegion()).build();
        }catch (NullPointerException e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("==============================================");
        System.out.println("Getting Started with Amazon SQS Standard Queue");
        System.out.println("==============================================");

        log.info("aws SQS Client init end");

    }

    public void send(Object object){

        ObjectMapper mapper = new ObjectMapper();
        try {

            String jsonString = mapper.writeValueAsString(object);

            //SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(sqsConfig.getUrl())
            //        .withMessageBody(object.toString());

            SendMessageRequest sendMessageRequest = new SendMessageRequest(sqsConfig.getUrl(), jsonString);

            //System.out.println(sendMessageRequest);
            log.info("aws SQS Message ID -> {}", sqs.sendMessage(sendMessageRequest));

            log.info("aws SQS Message send Data -> {}", jsonString);
        }catch (Exception e){
            log.error(e.toString());
        }

    }

    //@Scheduled(fixedRate = 2000)
    public void receive(){

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsConfig.getUrl())
                .withWaitTimeSeconds(2).withMaxNumberOfMessages(1);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

        log.info("aws SQS Message receive Data -> {}", messages);
    }



}
