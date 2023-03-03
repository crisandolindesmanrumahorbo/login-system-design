package com.rumahorbo.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RestService {

    private final Logger logger = LoggerFactory.getLogger(RestService.class);

    @Value("${BASE_URL}")
    private String baseUrl;

    public <T> T post(String url, BodyInserters.FormInserter<String> requestBody, Class<T> responseBodyClass) {
        T result;
        try {
            result = WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .post()
                    .uri(url)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError,
                            error -> Mono.error(new RuntimeException("API Not Found")))
                    .onStatus(HttpStatus::is5xxServerError,
                            error -> Mono.error(new RuntimeException(error.statusCode().getReasonPhrase())))
                    .bodyToMono(responseBodyClass)
                    .block();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            result = null;
        }
        return result;
    }

    public HttpStatus logout(String logoutUrl, BodyInserters.FormInserter<String> logoutRequestBody, String clientId, String clientSecret) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(clientId, clientSecret))
                .build()
                .post()
                .uri(logoutUrl)
                .body(logoutRequestBody)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .block();
    }
}
