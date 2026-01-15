package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 추상 클래스로 선언하여 단독으로 생성되지 않도록 막음
public abstract class BaseEntity {
    // === 1 필드 ===
    // protected final : 나와 같은 패키지 or 다른 패키지 && 상속받은 자식 클래스만 접근 가능 / 수정 불가 필드
    protected final UUID id;  // 객체를 식별하기 위한 id로 UUID 타입
    // 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드 (Long)
    protected final Long createdAt;
    protected Long updatedAt;  // updatedAt은 수정 될 수 있는 필드여야 함

    // === 2 생성자 ===
    public BaseEntity() {
        this.id = UUID.randomUUID();  // 랜덤 UUID 객체 반환
        long now = System.currentTimeMillis();  // 현재 시간 now에 저장
        this.createdAt = now;
        this.updatedAt = now;
    }

    // === 4 Getter ===
    public UUID getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt; // -> 얘는 어디서 쓰일까?
    }

    // === 5 update ===
    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}