package com.sprint.mission.discodeit.entity;
import com.sprint.mission.discodeit.dto.MessageDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Getter, update
    // field
    @Getter
    private String content;
    @Getter
    private final User user;
    @Getter
    private final Channel channel;
    @Getter
    private List<UUID> binaryContentIds;

    // constructor
    public Message(MessageDto.MessageRequest request, List<UUID> binaryContentIds) {
        this.content = request.content();
        this.user = request.user();
        this.channel = request.channel();
        this.binaryContentIds = binaryContentIds;
    }

    public UUID getUserId() {
        return user.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    // 메세지 수정
    public void updateContent(String content) {
        this.content = content;
        updateTimestamp();
    }
}
