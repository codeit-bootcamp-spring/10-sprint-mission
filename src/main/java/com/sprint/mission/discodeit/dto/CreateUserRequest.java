package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private ProfileImageData profileImage; // 선택적

    public CreateUserRequest(String username, String email, String password, ProfileImageData profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

//    // Getters
//    public String getUsername() { return username; }
//    public String getEmail() { return email; }
//    public String getPassword() { return password; }
//    public ProfileImageData getProfileImage() { return profileImage; }
}
