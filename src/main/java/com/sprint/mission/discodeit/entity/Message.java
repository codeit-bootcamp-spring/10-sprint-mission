package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Instant updatedAt;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentId) {
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = (attachmentId!= null) ? attachmentId: new ArrayList<>(); // 내용을 받지 않았을 때에는 빈 리스트로 초기화
    }

    public void update(String newContent, List<UUID> newAttachmentIds) {
        boolean[] isUpdated = {false};

        Optional.ofNullable(newContent)
                .filter(c -> !c.equals(this.content))
                .ifPresent(c -> {
                    this.content = c;
                    isUpdated[0] = true;
                });

        Optional.ofNullable(newAttachmentIds)
                .filter(ids -> !ids.equals(this.attachmentIds))
                .ifPresent(ids -> {
                    this.attachmentIds = ids;
                    isUpdated[0] = true;
                });

        // 하나라도 바뀌었으면 시간 갱신
        if (isUpdated[0]) {
            this.updatedAt = Instant.now();
        }
    }
}
