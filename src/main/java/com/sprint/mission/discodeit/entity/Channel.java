package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // 베이스 코드를 참고하여, 필드 이름 변경: channelName -> name
    private String name;
    private String description;
    private ChannelType type;

    private final List<UUID> memberIds = new ArrayList<>();

    public Channel(String name, String description, ChannelType type) {
        if (type == ChannelType.PUBLIC) validateChannel(name);
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;

        if (newName != null && !newName.equals(this.name)) {
            validateChannel(newName);
            this.name = newName;
            anyValueUpdated = true;
        }

        // 설명(description)은 null이거나 비어있을 수도 있으니 동등 비교만 수행
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.update();
        }
    }

    // 채널 참여
    public void addMember(UUID userId) {
        if (userId == null) throw new BusinessException(ErrorCode.REQUIRED_PARAMETER_MISSING);
        if (memberIds.contains(userId)) throw new BusinessException(ErrorCode.ALREADY_IN_CHANNEL);

        memberIds.add(userId);
    }

    // 채널 퇴장
    public void removeMember(UUID userId) {
        if (userId == null) throw new BusinessException(ErrorCode.REQUIRED_PARAMETER_MISSING);
        if (!memberIds.contains(userId)) throw new BusinessException(ErrorCode.NOT_A_MEMBER);

        memberIds.remove(userId);
    }

    // 채널 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    private void validateChannel(String channelName) {
        // null, Blank 체크
        if (channelName == null || channelName.isEmpty() ||
                channelName.length() < 2 || channelName.length() > 15)
            throw new BusinessException(ErrorCode.INVALID_CHANNEL_NAME);
    }

    @Override
    public String toString() {
        return String.format("Channel[이름: %s, Channel ID: %s]", name, getId());
    }

    public List<UUID> getMemberIds() {
        return new ArrayList<>(memberIds);
    }
}