package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


@Getter
public class Message extends BaseEntity implements Serializable {

        private final UUID channelId;
        private final UUID userId;
        private String content;
        private List<UUID> attachmentIds;

        public Message(UUID channelId, UUID userId, String content, List<UUID> attachmentIds) {
            super();
            this.channelId = channelId;
            this.userId = userId;
            this.content = content;
            this.attachmentIds = attachmentIds != null ? attachmentIds : List.of();
        }

        public void updateContent(String content) {
            this.content = content;
            touch();
        }
}