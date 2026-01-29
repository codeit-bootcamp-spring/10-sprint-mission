package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class User extends DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        super();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void isUpdated(){
        this.updatedAt = Instant.now();
    }
}
