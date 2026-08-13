package com.gaurav.aiproductivity.dto.chat;

public record ChatStreamEvent(
        String type,
        String streamId,
        String content
) {
}