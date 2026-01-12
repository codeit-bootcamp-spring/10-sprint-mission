package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity{
    private String name;
    private String email;

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public void updateName(String name) {
            if(name != null && !name.isBlank()) {
                this.name = name;
                updateTimestamps();
            }
    }
    public void updateEmail(String email) {
        if (email != null && !email.isBlank()) {
            this.email = email;
            updateTimestamps();
        }
    }
    @Override
    public String toString() {
        return "유저[이름: " + name +
                ", 이메일: " + email + "]";
    }
}
