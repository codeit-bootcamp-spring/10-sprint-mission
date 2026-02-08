package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.ChannelDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum channelType {PRIVATE, PUBLIC}
    // 채널에 속한 메세지 목록
    @Getter
    private final List<UUID> messageIds = new ArrayList<>();
    // 채널에 참여한 유저 목록
    @Getter
    private final List<UUID> userIds = new ArrayList<>();

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private final channelType type;
    @Getter
    private Instant lastMessageAt;
    // private 채널을 식별하기 위한 UUID 앞 8자리 식별자
    @Getter
    private String privateServerId;

    // Public 생성자
    public Channel(ChannelDto.ChannelRequest request) {
        this.name = Objects.requireNonNull(request.name(), "채널명은 필수입니다.");
        this.description = Objects.requireNonNull(request.description(), "채널 설명은 필수입니다.");
        this.type = channelType.PUBLIC;
        this.lastMessageAt = Instant.now();
    }

    // Private 생성자
    public Channel(List<UUID> userIds){
        this.userIds.addAll(Objects.requireNonNull(userIds, "유효한 유저를 입력해주세요."));
        this.type = channelType.PRIVATE;
        this.lastMessageAt = Instant.now();
        this.privateServerId = this.getId().toString().substring(0, 8);
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    // 유저 목록에 유저 추가
    public void addUser(User user) {
        if (user == null)
            return;
        if (!this.userIds.contains(user.getId()))
            this.userIds.add(user.getId());
    }

    // 유저 목록에서 유저 삭제
    public void removeUser(User user) {
        userIds.remove(user.getId());
    }

    // 메세지 목록에 메세지 추가 + 채널 수정 시간 갱신
    public void addMessage(Message message) {
        this.messageIds.add(message.getId());
        this.lastMessageAt = Instant.now();
        this.updateTimestamp();
    }

    // 메세지 목록에서 메세지 삭제 + 채널 수정 시간 갱신
    public void removeMessage(UUID messageId) {
        this.messageIds.removeIf(m -> m.equals(messageId));
        this.updateTimestamp();
    }

}
