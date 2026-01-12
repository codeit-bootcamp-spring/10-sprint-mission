package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class ChannelPermission extends BaseEntity {
    private UUID channelId;
    private UUID targetId;
    private PermissionTarget type; // USER, ROLE

    // 나중에 여기에 권한에 따른 읽기 쓰기 등 추가 가능, 확장성 고려

    public ChannelPermission(UUID channelId, UUID targetId, PermissionTarget type) {
        super();
        this.channelId = channelId;
        this.targetId = targetId;
        this.type = type;
    }

    // Getters
    public UUID getChannelId() {
        return channelId; }

    public UUID getTargetId() {
        return targetId; }

    public PermissionTarget getType() {
        return type; }

}
