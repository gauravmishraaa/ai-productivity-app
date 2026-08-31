package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.conversation.ConversationResponse;
import com.gaurav.aiproductivity.dto.conversation.CreateConversationRequest;
import com.gaurav.aiproductivity.dto.conversation.UpdateConversationRequest;
import com.gaurav.aiproductivity.entity.Conversation;
import com.gaurav.aiproductivity.exception.ConversationNotFoundException;
import com.gaurav.aiproductivity.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatService chatService;

    @Override
    public ConversationResponse create(
            CreateConversationRequest request
    ) {

        Conversation conversation = Conversation.builder()
                .title(request.title())
                .build();
        Conversation saved =
                conversationRepository.save(conversation);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getAll() {

        return conversationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getById(Long id) {
        Conversation conversation =
                conversationRepository.findById(id)
                        .orElseThrow(() ->
                                new ConversationNotFoundException(id)
                        );

        return toResponse(conversation);
    }

    @Override
    public ConversationResponse update(
            Long id,
            UpdateConversationRequest request
    ) {

        Conversation conversation =
                conversationRepository.findById(id)
                        .orElseThrow(() ->
                                new ConversationNotFoundException(id)
                        );
        conversation.setTitle(request.title());
        Conversation updated =
                conversationRepository.save(conversation);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        conversationRepository.findById(id)
                .orElseThrow(() ->
                        new ConversationNotFoundException(id)
                );

        chatService.deleteHistory(id);
        conversationRepository.deleteById(id);
    }

    private ConversationResponse toResponse(
            Conversation conversation
    ) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}