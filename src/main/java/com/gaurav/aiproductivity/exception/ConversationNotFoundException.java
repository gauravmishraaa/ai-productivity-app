package com.gaurav.aiproductivity.exception;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(Long id) {
        super("Conversation not found with id: " + id);
    }
}