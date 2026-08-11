package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Override
    public ChatResponse chat(
            Long conversationId,
            String message
    ) {

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
    public List<ChatMessageResponse> getHistory(
            Long conversationId
    ) {

        List<Message> messages = chatMemory.get(
                conversationId.toString()
        );

        return messages.stream()
                .map(message -> {

                    String role = message.getMessageType()
                            .name();

                    return new ChatMessageResponse(
                            role,
                            message.getText()
                    );
                })
                .toList();
    }
}