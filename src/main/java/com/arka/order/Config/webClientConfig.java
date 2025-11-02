package com.arka.order.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {
    @Bean
    public WebClient productWebClient(){
        return WebClient.builder()
                .baseUrl("http://ec2-18-216-70-5.us-east-2.compute.amazonaws.com:8081")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
