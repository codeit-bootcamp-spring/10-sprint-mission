package org.example.entity;

import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    public User (String username, String email, String password, String nickname, String profileImage) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void update(String username, String email, String password, String nickname, String profileImage){
        this.username = username;
        this.email = email;
        this.password = password;   //비번은 따로 변경하게 해야 할거 같음
        this.nickname = nickname;
        this.profileImage = profileImage;
        super.updatedAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
