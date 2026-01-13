package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;
    private boolean isPublic;

    private final Set<ChannelPermission> permissions = new HashSet<>();

    // List는 멀티쓰레딩 환경에서 순서 보장 안됨
    // 나중에 리팩토링 가능성 존재
    private final List<Message> messages = new ArrayList<>();

    public Channel(String channelName, boolean isPublic){
        super();
        this.channelName = channelName;
        this.isPublic = isPublic;
    }

    // Getters
    public String getChannelName() {
        return channelName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Updates
    public void updateName(String newName){
        this.channelName = newName;
        updateTimestamp();
    }

    public void updatePublic(boolean isPublic){
        this.isPublic = isPublic;
        updateTimestamp();
    }

    // Authorized method
    public void addPermission(UUID targetId, PermissionTarget type) {
        boolean exists = this.permissions.stream()
                .anyMatch(p -> p.getTargetId().equals(targetId));

        if (!exists) {
            ChannelPermission permission = new ChannelPermission(
                    this.getId(),
                    targetId,
                    type
            );

            this.permissions.add(permission);
        }
    }

    public void removePermission(UUID targetId) {
        boolean removed = this.permissions.removeIf(p -> p.getTargetId().equals(targetId));

        if (removed) {
            updateTimestamp();
        }
    }

    public boolean isAccessibleBy(User user) {
        if (this.isPublic) return true;

        for (ChannelPermission permission : permissions) {
            UUID targetId = permission.getTargetId();

            if (permission.getType() == PermissionTarget.USER
                    && targetId.equals(user.getId())) {
                return true;
            }

            if (permission.getType() == PermissionTarget.ROLE
                    && user.getRoleIds().contains(targetId)) {
                return true;
            }
        }
        return false;
    }

    // Message Control
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return this.messages;
    }

}