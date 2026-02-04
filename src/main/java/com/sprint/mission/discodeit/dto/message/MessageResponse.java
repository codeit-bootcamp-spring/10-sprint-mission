package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID channelId;
    private UserResponse author;
    private List<UUID> attachmentIds; // 첨부파일 ID 목록
    private Instant createdAt;
    private Instant updatedAt;

    //Message 엔티티로부터 변환
    public static MessageResponse from(Message message, UserResponse author, List<UUID> attachmentIds) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                author,
                attachmentIds,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }

}
