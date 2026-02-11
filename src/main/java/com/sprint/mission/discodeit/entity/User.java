package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;


@Getter
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String password;
    private UUID profileImageId;

    public User(String name, String email, String password) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
        touch(); // 수정시간 갱신
    }

    public void updateEmail(String email) {
        this.email = email;
        touch();
    }

    public void updatePassword(String password) {
        this.password = password;
        touch();
    }

    public void updateProfileImage(UUID imageId) {
        this.profileImageId = imageId;
        touch();
    }

    @Override
    public String toString() {
        return "이름: " + name + "\n" + "email: " + email;
    }
}