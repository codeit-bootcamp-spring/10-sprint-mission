package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private boolean isPublic;

    private Set<ChannelPermission> permissions;

    public Channel(String channelName, boolean isPublic){
        super();
        this.channelName = channelName;
        this.isPublic = isPublic;
        this.permissions = new HashSet<>();
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

    // Logic
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




}