/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.client;

import com.trangnhk.dto.ai.AiChatRequestDTO;
import com.trangnhk.dto.ai.AiChatResponseDTO;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Admin
 */
@Component
public class AiServiceClient {

    private final RestClient restClient;
    private final String chatEndpoint;

    public AiServiceClient(
            @Value("${ai.service.base-url}") String baseUrl,
            @Value("${ai.service.chat-endpoint}") String chatEndpoint
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "ai.service.base-url is not configured."
            );
        }

        if (chatEndpoint == null || chatEndpoint.isBlank()) {
            throw new IllegalArgumentException(
                    "ai.service.chat-endpoint is not configured."
            );
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        this.chatEndpoint = chatEndpoint;
    }

    public AiChatResponseDTO chat(AiChatRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException("AiChatRequestDTO cannot be null.");
        }

        System.out.println("=================================");
        System.out.println("CALLING AI SERVICE");
        System.out.println("Endpoint: " + chatEndpoint);
        System.out.println("Message: " + request.getMessage());
        System.out.println(
                "Conversation History: "
                + request.getConversationHistory()
        );
        System.out.println(
                "Conversation State: "
                + request.getConversationState()
        );
        System.out.println("Top K: " + request.getTopK());
        System.out.println("=================================");

        try {
            return restClient
                    .post()
                    .uri(chatEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiChatResponseDTO.class);

        } catch (HttpClientErrorException e) {
            System.err.println("AI SERVICE CLIENT ERROR: " + e.getStatusCode());
            System.err.println("Response: " + e.getResponseBodyAsString());
            throw e;

        } catch (RestClientException e) {
            System.err.println("AI SERVICE CONNECTION ERROR: " + e.getMessage());
            throw e;
        }

    }
}
