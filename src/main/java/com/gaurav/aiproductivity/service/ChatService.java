package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.dto.chat.ChatStreamEvent;
import com.gaurav.aiproductivity.dto.chat.StreamControlResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    // Normal chat
    ChatResponse chat(
            Long conversationId,
            String message
    );

    // Start streaming
    Flux<ChatStreamEvent> streamChat(
            Long conversationId,
            String message
    );

    // Get chat history
    List<ChatMessageResponse> getHistory(
            Long conversationId
    );

    // Delete chat history
    void deleteHistory(
            Long conversationId
    );

    // Pause active stream
    StreamControlResponse pauseStream(
            String streamId
    );

    // Resume paused stream
    Flux<ChatStreamEvent> resumeStream(
            String streamId
    );
}