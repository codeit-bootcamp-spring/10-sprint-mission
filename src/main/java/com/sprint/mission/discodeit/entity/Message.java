package com.sprint.mission.discodeit.entity;
import com.sprint.mission.discodeit.dto.MessageDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Getter, update
    // field
    @Getter
    private String content;
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;
    @Getter
    private final List<UUID> binaryContentIds;

    // constructor
    public Message(MessageDto.MessageCreateRequest request, List<UUID> binaryContentIds) {
        this.content = request.content();
        this.userId = Objects.requireNonNull(request.userId(), "유효한 사용자 ID를 입력해주세요.");
        this.channelId = Objects.requireNonNull(request.channelId(), "유효한 채널 ID를 입력해주세요.");
        this.binaryContentIds = Objects.requireNonNull(binaryContentIds, "유효한 자료 ID를 입력해주세요.");
    }

    // 메세지 수정
    public void updateContent(String content) {
        this.content = content;
        updateTimestamp();
    }

    public void updateBinaryContentIds(List<UUID> binaryContentIds) {
        this.binaryContentIds.clear();
        this.binaryContentIds.addAll(binaryContentIds);
        updateTimestamp();
    }
}
