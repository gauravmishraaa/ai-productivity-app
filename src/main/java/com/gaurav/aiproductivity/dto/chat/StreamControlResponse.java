package com.gaurav.aiproductivity.dto.chat;

public record StreamControlResponse(
        String streamId,
        String state,
        String partialResponse
) {
}