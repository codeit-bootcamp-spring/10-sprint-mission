package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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
        if (authorId == null || channelId == null) throw new BusinessException(ErrorCode.REQUIRED_PARAMETER_MISSING);
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
        if (content == null || content.isBlank()) throw new BusinessException(ErrorCode.EMPTY_MESSAGE_CONTENT);

        // 메세지 길이 체크 (1자 이상, 500자 이하)
        if (content.length() > 500) throw new BusinessException(ErrorCode.MESSAGE_TOO_LONG);
    }

    @Override
    public String toString() {
        return String.format("Message[내용: %s, 작성자ID: %s, 채널ID: %s, 첨부 파일 수: %d, Message ID: %s]",
                content, authorId, channelId, attachmentIds.size(), getId());
    }
}
