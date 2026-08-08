package com.gaurav.aiproductivity.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatRequest(

        @NotNull(message = "Conversation ID cannot be null")
        Long conversationId,

        @NotBlank(message = "Message cannot be blank")
        @Size(
                max = 10000,
                message = "Message cannot exceed 10000 characters"
        )
        String message

) {
}