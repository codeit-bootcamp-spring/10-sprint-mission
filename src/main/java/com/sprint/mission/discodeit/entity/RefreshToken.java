package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseUpdatableEntity{

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false, unique = true)
    private String email;

    public RefreshToken(String email, String token) {
        this.token = token;
        this.email = email;
    }

    public void updateToken(String token){
        this.token = token;
    }

}
