package com.sprint.mission.discodeit.entity;

public class Role extends BaseEntity {
    private String roleName;

    public Role(String roleName) {
        super();
        this.roleName = roleName;
    }

    // Getter
    public String getRoleName() {
        return roleName;
    }

    // Update
    public void updateRoleName(String roleName) {
        this.roleName = roleName;
        this.updatedAt = System.currentTimeMillis();
    }
}