package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(exclude = {"user", "channel"})
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String content;
    private final User user;
    private final Channel channel;
    private boolean isEdited; // 수정 여부
    private boolean isPinned; // 고정 여부

    @Getter(AccessLevel.NONE)
    private final List<UUID> attachmentIds = new ArrayList<>(); // 첨부파일 목록 (BinaryContent 참조 ID)

    public Message(String content, User user, Channel channel) {
        super();
        this.content = content;
        this.user = user;
        this.channel = channel;
        this.isEdited = false;
        this.isPinned = false;
    }

    // 메시지 수정
    public void update(String content) {
        this.content = content;
        this.isEdited = true;
        this.updated();
    }

    // 메시지 고정/해제
    public void togglePin() {
        this.isPinned = !this.isPinned;
    }

    // 메시지 첨부파일 추가
    public void addAttachment(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
        this.updated();
    }

    // --- getter ---
    public List<UUID> getAttachmentIds() {
        return Collections.unmodifiableList(attachmentIds);
    }
}
