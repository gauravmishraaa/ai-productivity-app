package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatResponse;

import reactor.core.publisher.Flux;

public interface ChatService {

    ChatResponse chat(String message);

    Flux<String> streamChat(String message);
}