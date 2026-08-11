package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.entity.Conversation;
import com.gaurav.aiproductivity.exception.ConversationNotFoundException;
import com.gaurav.aiproductivity.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ConversationRepository conversationRepository;

    @Override
    public ChatResponse chat(
            Long conversationId,
            String message
    ) {

        validateConversation(conversationId);

        String response = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId.toString()
                        )
                )
                .call()
                .content();

        return new ChatResponse(response);
    }

    @Override
    public Flux<String> streamChat(
            Long conversationId,
            String message
    ) {

        validateConversation(conversationId);

        return chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId.toString()
                        )
                )
                .stream()
                .content();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(
            Long conversationId
    ) {

        validateConversation(conversationId);

        List<Message> messages =
                chatMemory.get(conversationId.toString());

        return messages.stream()
                .map(message ->
                        new ChatMessageResponse(
                                message.getMessageType().name(),
                                message.getText()
                        )
                )
                .toList();
    }

    @Override
    public void deleteHistory(Long conversationId) {

        validateConversation(conversationId);

        chatMemory.clear(
                conversationId.toString()
        );
    }

    private void validateConversation(Long conversationId) {

        conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new ConversationNotFoundException(
                                conversationId
                        )
                );
    }
}