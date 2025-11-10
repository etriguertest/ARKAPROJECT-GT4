package com.arka.order.Service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Service;


@Service
public class MessageProducerService {
    private final SqsTemplate sqsTemplate;
    private static final String QUEUE_NAME="queue-to-notification";

    public MessageProducerService(SqsTemplate sqsTemplate){
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String messagePayload){
        sqsTemplate.send(QUEUE_NAME,messagePayload);
        System.out.println("Message send to SQS: "+ messagePayload);
    }
}
