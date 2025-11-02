package com.arka.inventory.aws;

// NEW IMPORTS from AWS SDK v2

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SnsPublisherService {

    // 1. Client type changed from AmazonSNS to SnsClient
    private final SnsClient snsClient;
    private final String topicArn = "arn:aws:sns:us-east-1:611707305023:TransactionTopic";

    // 2. Constructor dependency changed to SnsClient
    public SnsPublisherService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publishNotification(String subject, String message) {

        // 3. Use the v2 PublishRequest.builder()
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .subject(subject) // Subject is now included correctly
                .build();

        // 4. Call the v2 client
        PublishResponse publishResult = snsClient.publish(publishRequest);

        System.out.println("SNS Message ID: " + publishResult.messageId());
    }
}