package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {

    ChatResponse chat(Long conversationId, String message);

    Flux<String> streamChat(Long conversationId, String message);
}