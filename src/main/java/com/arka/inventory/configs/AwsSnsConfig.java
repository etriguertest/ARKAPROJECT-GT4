package com.arka.inventory.configs;

// NEW IMPORTS from AWS SDK v2

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsSnsConfig {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * Define y configura manualmente el bean SnsClient (AWS SDK v2).
     */
    @Bean
    public SnsClient snsClient() {
        System.out.println("VARIABLES" );
        System.out.println(region);
        // Use v2 builder components
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}