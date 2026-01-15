package com.sprint.mission.discodeit.entity;


import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message extends BaseEntity {

    private final UUID senderId;   // 보낸 사람 ID
    private String content;        // 메시지 내용

    public Message(UUID senderId, String content) {
        super();
        this.senderId = senderId;
        this.content = content;
    }

    // 메시지 내용 수정 / 시간 갱신
    public void updateContent(String newContent) {
        this.content = newContent;
        touch();
    }

    public UUID getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        Long updated = getUpdatedAt();
        long baseTime = (updated != null) ? updated : getCreatedAt();

        LocalTime time = Instant.ofEpochMilli(baseTime)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return content + " (" + time.format(formatter) + ")";
    }

}
