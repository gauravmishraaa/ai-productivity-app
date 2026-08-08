package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.conversation.ConversationResponse;
import com.gaurav.aiproductivity.dto.conversation.CreateConversationRequest;
import com.gaurav.aiproductivity.dto.conversation.UpdateConversationRequest;

import java.util.List;

public interface ConversationService {

    ConversationResponse create(CreateConversationRequest request);

    List<ConversationResponse> getAll();

    ConversationResponse getById(Long id);

    ConversationResponse update(
            Long id,
            UpdateConversationRequest request
    );

    void delete(Long id);
}