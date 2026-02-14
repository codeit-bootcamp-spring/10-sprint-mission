package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private final UUID authorId;
    private final UUID channelId;
    private String content;
    private final List<UUID> attachmentIds;

    public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
        if (authorId == null) throw new IllegalArgumentException("유저 정보가 유효하지 않습니다.");
        if (channelId == null) throw new IllegalArgumentException("채널 정보가 유효하지 않습니다.");
        validateContent(content);

        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = (attachmentIds != null) ? new ArrayList<>(attachmentIds) : new ArrayList<>();
    }

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            validateContent(newContent);
            this.content = newContent;
            super.update();
        }
    }

    // 메세지 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    // content.length() < 1는 항상 false를 반환하므로 작성 하지 않음
    private void validateContent(String content) {

        // null, Blank 체크
        if (content == null || content.isBlank()) throw new IllegalArgumentException("메세지 내용을 입력해주세요");

        // 메세지 길이 체크 (1자 이상, 500자 이하)
        if (content.length() > 500) throw new IllegalArgumentException("메세지는 1자 이상, 500자 이하로 작성해주세요.");
    }

    @Override
    public String toString() {
        return String.format("Message[내용: %s, 작성자ID: %s, 채널ID: %s, 첨부 파일 수: %d, Message ID: %s]",
                content, authorId, channelId, attachmentIds.size(), getId());
    }
}
