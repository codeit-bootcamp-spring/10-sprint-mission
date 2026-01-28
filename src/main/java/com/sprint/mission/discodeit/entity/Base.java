package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Base implements Serializable {
    private static final long serialVersionUID = 1L; // (역)직렬화 클래스의 버전

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
