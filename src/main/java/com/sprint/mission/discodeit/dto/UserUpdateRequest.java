package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import java.util.UUID;

@Getter
public class UserUpdateRequest {
    private final UUID id;
    private final String name;
    private final String email;
    private final String profileFileName;
    private final byte[] profileContent;
    private final String contentType;

    public UserUpdateRequest(UUID id, String name, String email, String profileFileName, byte[] profileContent, String contentType) {
        validate(id, name, email);
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileFileName = profileFileName;
        this.profileContent = profileContent;
        this.contentType = contentType;
    }

    public UserUpdateRequest(UUID id, String name, String email) {
        this(id, name, email, null, null, null);
    }

    private void validate(UUID id, String name, String email) {
        if (id == null) throw new IllegalArgumentException("ID는 필수입니다.");
        if (name != null && name.isBlank()) throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
        if (email != null && !email.contains("@")) throw new IllegalArgumentException("이메일 형식이 아닙니다.");
    }
}