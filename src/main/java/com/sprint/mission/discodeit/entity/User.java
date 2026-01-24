package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class User extends DefaultEntity {
    private static final long serialVersionUID = 1L;
    private String userName;
    private List<UUID> roles;

    public User(String userName) {
        this.userName = userName;
        roles = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return userName;
    }

    public List<UUID> getRoleIDs() {
        return roles;
    }

    public void AddRoleInUser(UUID roleID) {
        roles.add(roleID);
    }

    public void DeleteRoleInUser(UUID roleID) {
        roles.stream()
                .filter(role -> roleID.equals(role))
                .findFirst()
                .ifPresent(roles::remove);
    }

    public void setRoleIDs(List<UUID> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
