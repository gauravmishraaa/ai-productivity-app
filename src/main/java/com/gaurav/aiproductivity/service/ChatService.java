package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.dto.chat.ChatStreamEvent;
import com.gaurav.aiproductivity.dto.chat.StreamControlResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    ChatResponse chat(
            Long conversationId,
            String message
    );

    Flux<ChatStreamEvent> streamChat(
            Long conversationId,
            String message
    );

    List<ChatMessageResponse> getHistory(
            Long conversationId
    );

    void deleteHistory(
            Long conversationId
    );

    StreamControlResponse pauseStream(
            String streamId
    );
}