package com.arka.sales.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${external.order-service-base-url}")
    private String orderBaseUrl;

    @Value("${external.product-service-base-url}")
    private String productBaseUrl;

    @Value("${external.customer-service-base-url}")
    private String customerBaseUrl;

    @Value("${external.webclient.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Bean("orderWebClient")
    public WebClient orderWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(connectTimeoutMs));

        return WebClient.builder()
                .baseUrl(orderBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer -> {
                }).build())
                .build();
    }


    @Bean("productWebClient")
    public WebClient productWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(connectTimeoutMs));

        return WebClient.builder()
                .baseUrl(productBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer -> {
                }).build())
                .build();
    }


    @Bean("customerWebClient")
    public WebClient customerWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(connectTimeoutMs));

        return WebClient.builder()
                .baseUrl(customerBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer -> {
                }).build())
                .build();
    }
}
