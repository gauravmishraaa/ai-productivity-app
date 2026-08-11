package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    ChatResponse chat(
            Long conversationId,
            String message
    );

    Flux<String> streamChat(
            Long conversationId,
            String message
    );

    List<ChatMessageResponse> getHistory(
            Long conversationId
    );
}