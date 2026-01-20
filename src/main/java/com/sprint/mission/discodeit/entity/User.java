package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class User extends DefaultEntity {
    private String userName;
    private List<Role> roles;

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

    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
