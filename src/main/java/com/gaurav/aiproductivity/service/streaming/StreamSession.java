package com.gaurav.aiproductivity.service.streaming;

import lombok.Getter;
import lombok.Setter;
import reactor.core.Disposable;

@Getter
@Setter
public class StreamSession {

    private final String streamId;

    private final Long conversationId;

    private final String userMessage;

    private final StringBuilder partialResponse;

    private volatile StreamState state;

    private volatile Disposable subscription;

    public StreamSession(
            String streamId,
            Long conversationId,
            String userMessage
    ) {
        this.streamId = streamId;
        this.conversationId = conversationId;
        this.userMessage = userMessage;
        this.partialResponse = new StringBuilder();
        this.state = StreamState.ACTIVE;
    }

    public void appendResponse(String chunk) {

        if (chunk != null && !chunk.isEmpty()) {
            partialResponse.append(chunk);
        }
    }

    public String getPartialResponse() {

        return partialResponse.toString();
    }

    public void cancelSubscription() {

        Disposable currentSubscription = this.subscription;

        if (currentSubscription != null
                && !currentSubscription.isDisposed()) {

            currentSubscription.dispose();
        }
    }
}