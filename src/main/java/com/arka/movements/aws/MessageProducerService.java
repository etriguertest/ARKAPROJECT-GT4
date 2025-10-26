package com.arka.movements.aws;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {

    private final SqsTemplate sqsTemplate;
    private static final String QUEUE_NAME = "queue-to-ec2-movements";

    // SqsTemplate is auto-configured and injected
    public MessageProducerService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String messagePayload) {
        // Use the send method, specifying the queue name and the message payload
        sqsTemplate.send(QUEUE_NAME, messagePayload);

        // Alternatively, to send a complex object:
        // sqsTemplate.send(QUEUE_NAME, new MyObject("data", 123));

        System.out.println("Message sent to SQS queue: " + messagePayload);
    }
}