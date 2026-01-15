package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {

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
