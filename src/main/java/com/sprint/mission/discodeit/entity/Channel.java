package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private boolean isPublic;

    private Set<ChannelPermission> permissions;

    private Channel(String channelName, boolean isPublic){
        super();
        this.channelName = channelName;
        this.isPublic = isPublic;
        this.permissions = new HashSet<>();
    }

    // Authorized method
    public void addPermission(User user) {
        ChannelPermission permission = new ChannelPermission(
                this.getId(),
                user.getId(),
                PermissionTarget.USER
        );
        this.permissions.add(permission);
        updateTimestamp();
    }

    public void addPermission(Role role) {
        ChannelPermission permission = new ChannelPermission(
                this.getId(),
                role.getId(),
                PermissionTarget.ROLE
        );
        this.permissions.add(permission);
        updateTimestamp();
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