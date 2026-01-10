package com.sprint.mission.discodeit.entity;

public class User extends Entity {
    private final String email;
    private String nickname;

    public User(String nickname, String email) {
        super();
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public User update(String nickname) {
        super.update();
        this.nickname = nickname;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "User [id=%s, nickname=%s, email=%s]",
                getId().toString().substring(0, 5),
                nickname,
                email
        );
    }

}
