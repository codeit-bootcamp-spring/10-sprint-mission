package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    // 메시지 내용
    private String content;
    // 유저 정보
    private UUID sentUserId;
    // 채널 정보
    private UUID sentChannelId;
    // 연결되어 있는 id들
    private List<UUID> attachmentIds;

    public Message(UUID sentUserId, UUID sentChannelId, String content) {
        // id 자동생성 및 초기화
        super();
        // content 초기화
        this.content = content;
        // sentUserId 초기화는 메시지 전송 시점에 설정
        this.sentUserId = sentUserId;
        // sentChannelId 초기화는 메시지 전송 시점에 설정
        this.sentChannelId = sentChannelId;
        this.attachmentIds = new ArrayList<>();
    }

    public void updateContent(String content) {
        this.content = content;
        setUpdatedAt();
    }

    public void updateAttachmentIds(UUID attachmentId) {
        attachmentIds.add(attachmentId);
    }

    public void removeAttachmentIds(UUID attachmentId) {
        attachmentIds.removeIf(id -> id.equals(attachmentId));
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", sentUser='" + sentUserId + '\'' +
                ", sentChannel='" + sentChannelId + '\'' +
                ", attachmentIds='" + attachmentIds + '\'' +
                '}';
    }
}
