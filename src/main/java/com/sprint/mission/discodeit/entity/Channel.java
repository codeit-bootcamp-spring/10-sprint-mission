package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequestDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String channelName;         // 채널 이름 (변경 가능)
    private UUID userId;                // 채널 소유자 (변경 불가능)
    private List<UUID> members;         // 채널 참가자 (변경 가능)
    private ChannelType type;           // PUBLIC, PRIVATE (변경 불가능)
    private String description;         // 채널 설명 (변경 가능)

    public Channel(PublicChannelCreateRequestDTO publicChannelCreateRequestDTO) {
        this.members = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.channelName = publicChannelCreateRequestDTO.getChannelName();
        this.userId = publicChannelCreateRequestDTO.getUserId();
        this.type = ChannelType.PUBLIC;
        this.description = publicChannelCreateRequestDTO.getDescription();

        this.members.add(userId);
    }

    public Channel(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = privateChannelCreateRequestDTO.getUserId();
        this.type = ChannelType.PRIVATE;
        this.members = new ArrayList<>(privateChannelCreateRequestDTO.getMemberIds());
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = Instant.now();
    }

    public void updateChannelDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void addMember(UUID memberId) {
        this.members.add(memberId);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }
}