package com.sprint.mission.dto;

import java.util.UUID;

public class SendMsgVerificationRequest {
    private final UUID userId;
    private final UUID channelId;

    private SendMsgVerificationRequest(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public static SendMsgVerificationRequest of(UUID userId, UUID channelId) {
        return new SendMsgVerificationRequest(userId, channelId);
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }
}
