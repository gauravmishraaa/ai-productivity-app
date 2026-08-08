package com.gaurav.aiproductivity.controller;

import com.gaurav.aiproductivity.dto.common.ApiResponse;
import com.gaurav.aiproductivity.dto.conversation.ConversationResponse;
import com.gaurav.aiproductivity.dto.conversation.CreateConversationRequest;
import com.gaurav.aiproductivity.dto.conversation.UpdateConversationRequest;
import com.gaurav.aiproductivity.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponse>> create(
            @Valid @RequestBody CreateConversationRequest request
    ) {

        ConversationResponse response =
                conversationService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Conversation created successfully",
                                response
                        )
                );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getAll() {

        List<ConversationResponse> conversations =
                conversationService.getAll();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversations retrieved successfully",
                        conversations
                )
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationResponse>> getById(
            @PathVariable Long id
    ) {

        ConversationResponse response =
                conversationService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversation retrieved successfully",
                        response
                )
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConversationRequest request
    ) {

        ConversationResponse response =
                conversationService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversation updated successfully",
                        response
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        conversationService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversation deleted successfully",
                        null
                )
        );
    }
}