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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

        log.info("aws SQS Message send");

        ObjectMapper mapper = new ObjectMapper();
        try {

            JSONParser parser = new JSONParser();
            String jsonString = mapper.writeValueAsString(object);
            Object temp = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject)temp;

            SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(sqsConfig.getUrl())
                    .withMessageBody(object.toString())
                    .withDelaySeconds(1);

            //System.out.println(sendMessageRequest);
            sqs.sendMessage(sendMessageRequest);

            log.info("aws SQS Message Data -> {}", object.toString());
        }catch (Exception e){
            log.error(e.toString());
        }

    }

    //@Scheduled(fixedRate = 1000)
    public void receive(){

        log.info("aws SQS Message receive");

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsConfig.getUrl())
                .withWaitTimeSeconds(2).withMaxNumberOfMessages(10);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

        for(Message message : messages){
            System.out.println(message.toString());
        }

        log.info("aws SQS Message Data -> {}", messages);
    }



}
