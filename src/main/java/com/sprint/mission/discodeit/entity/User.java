package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String username;
//    private String password;
//    private String email;

    public User(String username) {
        super(); // ID 생성, 생성시간 기록
        // null 이거나 공백 제거하면 empty이면
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수");
        }
        // [ ] 특수문자, 길이제한 등등 추가
        this.username = username;
    }

    // Getter
    public String getUsername() {
        return username;
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("변경할 이름이 비어있음");
        }
        this.username = newUsername;
        this.updateTimestamp();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}