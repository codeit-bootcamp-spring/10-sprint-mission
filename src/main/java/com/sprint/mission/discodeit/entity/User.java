package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class User extends BaseEntity implements Serializable {

    private String name;
    private String email;

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        touch();
    }

    public String getUserName() {
        return name;
    }

    public String getUserEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "이름: " + name + "\n" + "email: " + email;
    }
}