package com.gaurav.aiproductivity.controller;

import com.gaurav.aiproductivity.dto.chat.ChatRequest;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.dto.common.ApiResponse;
import com.gaurav.aiproductivity.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request
    ) {

        ChatResponse response = chatService.chat(request.message());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Chat response generated successfully",
                        response
                )
        );
    }

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> streamChat(
            @Valid @RequestBody ChatRequest request
    ) {

        return chatService.streamChat(request.message());
    }
}