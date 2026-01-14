package com.sprint.mission.discodeit.entity;


public class User extends BaseEntity {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String status;

    public User(String name, String nickname, String email, String password) {
        super();
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.status = "OFFLINE";
    }

    // 모든 정보 수정 (이름, 닉네임, 이메일)
    public void update(String name, String nickname, String email) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.updated(); // BaseEntity의 시간 갱신
    }

    // 상태만 따로 수정
    public void updateStatus(String status) {
        this.status = status;
        this.updated();
    }

    // 비밀번호 변경
    public void updatePassword(String password) {
        this.password = password;
        this.updated();
    }

    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}