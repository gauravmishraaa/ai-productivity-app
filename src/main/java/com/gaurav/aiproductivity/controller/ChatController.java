package com.gaurav.aiproductivity.controller;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatRequest;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.dto.chat.ChatStreamEvent;
import com.gaurav.aiproductivity.dto.chat.StreamControlResponse;
import com.gaurav.aiproductivity.dto.common.ApiResponse;
import com.gaurav.aiproductivity.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    // =========================================================
    // NORMAL CHAT
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request
    ) {

        ChatResponse response =
                chatService.chat(
                        request.conversationId(),
                        request.message()
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Chat response generated successfully",
                        response
                )
        );
    }


    // =========================================================
    // START STREAM
    // =========================================================

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ChatStreamEvent> streamChat(
            @Valid @RequestBody ChatRequest request
    ) {

        return chatService.streamChat(
                request.conversationId(),
                request.message()
        );
    }


    // =========================================================
    // PAUSE STREAM
    // =========================================================

    @PostMapping("/stream/{streamId}/pause")
    public ResponseEntity<ApiResponse<StreamControlResponse>> pauseStream(
            @PathVariable String streamId
    ) {

        StreamControlResponse response =
                chatService.pauseStream(
                        streamId
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Stream paused successfully",
                        response
                )
        );
    }


    // =========================================================
    // RESUME STREAM
    // =========================================================

    @PostMapping(
            value = "/stream/{streamId}/resume",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ChatStreamEvent> resumeStream(
            @PathVariable String streamId
    ) {

        return chatService.resumeStream(
                streamId
        );
    }


    // =========================================================
    // CHAT HISTORY
    // =========================================================

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getHistory(
            @PathVariable Long conversationId
    ) {

        List<ChatMessageResponse> history =
                chatService.getHistory(
                        conversationId
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Chat history retrieved successfully",
                        history
                )
        )
    }
}