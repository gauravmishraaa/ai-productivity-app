package com.gaurav.aiproductivity.dto.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateConversationRequest(

        @NotBlank(message = "Title cannot be blank")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title

) {
}