package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    @Override
    public ChatResponse chat(String message) {

        String response = chatClient
                .prompt()
                .user(message)
                .call()
                .content();

        return new ChatResponse(response);
    }

    @Override
    public Flux<String> streamChat(String message) {

        return chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }
}