package com.gaurav.aiproductivity.service.streaming;

import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StreamManager {

    private final Map<String, StreamSession> sessions =
            new ConcurrentHashMap<>();

    // Create a new streaming session
    public StreamSession createSession(
            Long conversationId,
            String userMessage
    ) {

        String streamId = UUID.randomUUID().toString();

        StreamSession session = new StreamSession(
                streamId,
                conversationId,
                userMessage
        );

        sessions.put(streamId, session);

        return session;
    }

    // Get existing session
    public StreamSession getSession(String streamId) {

        return sessions.get(streamId);
    }

    // Check whether session exists
    public boolean exists(String streamId) {

        return sessions.containsKey(streamId);
    }

    // Register running reactive subscription
    public void registerSubscription(
            String streamId,
            Disposable subscription
    ) {

        StreamSession session = getRequiredSession(streamId);

        session.setSubscription(subscription);
    }

    // Append generated chunk
    public void appendResponse(
            String streamId,
            String chunk
    ) {

        StreamSession session = getRequiredSession(streamId);

        session.appendResponse(chunk);
    }

    // Pause current stream
    public void pause(String streamId) {

        StreamSession session = getRequiredSession(streamId);

        if (session.getState() != StreamState.ACTIVE) {
            return;
        }

        session.cancelSubscription();

        session.setState(StreamState.PAUSED);
    }

    // Mark stream active again
    public void resume(String streamId) {

        StreamSession session = getRequiredSession(streamId);

        if (session.getState() != StreamState.PAUSED) {
            return;
        }

        session.setState(StreamState.ACTIVE);
    }

    // Permanently cancel stream
    public void cancel(String streamId) {

        StreamSession session = getRequiredSession(streamId);

        session.cancelSubscription();

        session.setState(StreamState.CANCELLED);
    }

    // Mark stream completed
    public void complete(String streamId) {

        StreamSession session = getRequiredSession(streamId);

        session.setState(StreamState.COMPLETED);
    }

    // Mark stream failed
    public void fail(String streamId) {

        StreamSession session = getRequiredSession(streamId);

        session.setState(StreamState.FAILED);
    }

    // Get current state
    public StreamState getState(String streamId) {

        return getRequiredSession(streamId)
                .getState();
    }

    // Get generated partial response
    public String getPartialResponse(String streamId) {

        return getRequiredSession(streamId)
                .getPartialResponse();
    }

    // Get session or throw error
    private StreamSession getRequiredSession(
            String streamId
    ) {

        StreamSession session = sessions.get(streamId);

        if (session == null) {
            throw new IllegalArgumentException(
                    "Stream session not found: " + streamId
            );
        }

        return session;
    }

    // Remove session after it is no longer required
    public void remove(String streamId) {

        sessions.remove(streamId);
    }
}