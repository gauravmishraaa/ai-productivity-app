package com.gaurav.aiproductivity.service;

import com.gaurav.aiproductivity.dto.chat.ChatMessageResponse;
import com.gaurav.aiproductivity.dto.chat.ChatResponse;
import com.gaurav.aiproductivity.dto.chat.ChatStreamEvent;
import com.gaurav.aiproductivity.dto.chat.StreamControlResponse;
import com.gaurav.aiproductivity.exception.ConversationNotFoundException;
import com.gaurav.aiproductivity.repository.ConversationRepository;
import com.gaurav.aiproductivity.service.streaming.StreamManager;
import com.gaurav.aiproductivity.service.streaming.StreamSession;
import com.gaurav.aiproductivity.service.streaming.StreamState;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

    private final ConversationRepository conversationRepository;

    private final StreamManager streamManager;


    // =========================================================
    // NORMAL CHAT
    // =========================================================

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


    // =========================================================
    // START STREAM
    // =========================================================

    @Override
    public Flux<ChatStreamEvent> streamChat(
            Long conversationId,
            String message
    ) {

        validateConversation(conversationId);

        // Create a new stream session
        StreamSession session =
                streamManager.createSession(
                        conversationId,
                        message
                );

        String streamId =
                session.getStreamId();


        // Create AI streaming response
        Flux<String> aiStream = chatClient
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


        /*
         * IMPORTANT:
         *
         * First send STREAM_STARTED event.
         *
         * This gives frontend the streamId.
         *
         * Then start the actual AI stream.
         */

        return Flux.concat(

                // ---------------------------------------------
                // STREAM STARTED
                // ---------------------------------------------

                Flux.just(
                        new ChatStreamEvent(
                                "STREAM_STARTED",
                                streamId,
                                null
                        )
                ),

                // ---------------------------------------------
                // ACTUAL AI STREAM
                // ---------------------------------------------

                createControlledStream(
                        session,
                        aiStream
                )
        );
    }


    // =========================================================
    // PAUSE STREAM
    // =========================================================

    @Override
    public StreamControlResponse pauseStream(
            String streamId
    ) {

        StreamSession session =
                streamManager.getSession(streamId);

        if (session == null) {

            throw new IllegalArgumentException(
                    "Stream not found: " + streamId
            );
        }


        if (session.getState() != StreamState.ACTIVE) {

            throw new IllegalStateException(
                    "Stream is not active. Current state: "
                            + session.getState()
            );
        }


        // Cancel current reactive subscription
        streamManager.pause(streamId);


        return new StreamControlResponse(
                streamId,

                streamManager
                        .getState(streamId)
                        .name(),

                streamManager
                        .getPartialResponse(streamId)
        );
    }


    // =========================================================
    // RESUME STREAM
    // =========================================================

    @Override
    public Flux<ChatStreamEvent> resumeStream(
            String streamId
    ) {

        StreamSession session =
                streamManager.getSession(streamId);


        if (session == null) {

            throw new IllegalArgumentException(
                    "Stream not found: " + streamId
            );
        }


        if (session.getState() != StreamState.PAUSED) {

            throw new IllegalStateException(
                    "Stream cannot be resumed. Current state: "
                            + session.getState()
            );
        }


        Long conversationId =
                session.getConversationId();


        // Make sure conversation still exists
        validateConversation(conversationId);


        String partialResponse =
                session.getPartialResponse();


        /*
         * Build continuation prompt.
         *
         * We are NOT creating a new conversation.
         *
         * Same conversationId will be used.
         */

        String continuationPrompt = """
                Continue the answer from exactly where it stopped.

                Original user request:
                %s

                Already generated response:
                %s

                Instructions:
                - Continue naturally from the existing response.
                - Do not repeat the already generated text.
                - Do not start the answer again.
                - Return only the continuation.
                """.formatted(
                session.getUserMessage(),
                partialResponse
        );


        // Change state from PAUSED → ACTIVE
        streamManager.resume(streamId);


        // Start a NEW LLM inference
        Flux<String> aiStream = chatClient
                .prompt()
                .user(continuationPrompt)
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId.toString()
                        )
                )
                .stream()
                .content();


        /*
         * Resume event first.
         *
         * Frontend already knows streamId,
         * but sending RESUMED makes UI state handling easier.
         */

        return Flux.concat(

                Flux.just(
                        new ChatStreamEvent(
                                "RESUMED",
                                streamId,
                                null
                        )
                ),

                createControlledStream(
                        session,
                        aiStream
                )
        );
    }


    // =========================================================
    // CONTROLLED STREAM
    // =========================================================

    private Flux<ChatStreamEvent> createControlledStream(
            StreamSession session,
            Flux<String> aiStream
    ) {

        String streamId =
                session.getStreamId();


        return Flux.create(sink -> {

            /*
             * Subscribe to Ollama/Spring AI stream.
             */

            Disposable subscription = aiStream

                    // -----------------------------------------
                    // EACH AI CHUNK
                    // -----------------------------------------

                    .doOnNext(chunk -> {

                        // Save partial response
                        streamManager.appendResponse(
                                streamId,
                                chunk
                        );


                        // Send chunk to frontend
                        sink.next(
                                new ChatStreamEvent(
                                        "CHUNK",
                                        streamId,
                                        chunk
                                )
                        );
                    })


                    // -----------------------------------------
                    // AI GENERATION COMPLETED
                    // -----------------------------------------

                    .doOnComplete(() -> {

                        streamManager.complete(
                                streamId
                        );


                        sink.next(
                                new ChatStreamEvent(
                                        "COMPLETED",
                                        streamId,
                                        null
                                )
                        );


                        sink.complete();
                    })


                    // -----------------------------------------
                    // AI GENERATION FAILED
                    // -----------------------------------------

                    .doOnError(error -> {

                        streamManager.fail(
                                streamId
                        );


                        sink.next(
                                new ChatStreamEvent(
                                        "FAILED",
                                        streamId,
                                        error.getMessage()
                                )
                        );


                        sink.error(error);
                    })


                    .subscribe();


            /*
             * Store the running subscription.
             *
             * Pause/Cancel will use this Disposable.
             */

            streamManager.registerSubscription(
                    streamId,
                    subscription
            );


            /*
             * If client disconnects,
             * cancel the stream.
             */

            sink.onCancel(() -> {

                if (session.getState()
                        == StreamState.ACTIVE) {

                    streamManager.cancel(
                            streamId
                    );
                }
            });
        });
    }


    // =========================================================
    // CHAT HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(
            Long conversationId
    ) {

        validateConversation(conversationId);


        List<Message> messages =
                chatMemory.get(
                        conversationId.toString()
                );


        return messages.stream()
                .map(message ->
                        new ChatMessageResponse(
                                message.getMessageType().name(),
                                message.getText()
                        )
                )
                .toList();
    }


    // =========================================================
    // DELETE CHAT HISTORY
    // =========================================================

    @Override
    public void deleteHistory(
            Long conversationId
    ) {

        validateConversation(conversationId);


        chatMemory.clear(
                conversationId.toString()
        );
    }


    // =========================================================
    // VALIDATE CONVERSATION
    // =========================================================

    private void validateConversation(
            Long conversationId
    ) {

        conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new ConversationNotFoundException(
                                conversationId
                        )
                );
    }
}