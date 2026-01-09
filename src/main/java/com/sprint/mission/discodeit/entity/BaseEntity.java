// 공통 - 도메인 모델 정의

package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 추상 클래스로 선언하여 단독으로 생성되지 않도록 막음
public abstract class BaseEntity {
    // protected final : 나와 같은 패키지 or 다른 패키지 && 상속받은 자식 클래스만 / 수정 불가 필드

    // 객체를 식별하기 위한 id로 UUID 타입
    protected final UUID id;
    // 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드 (Long)
    protected final Long createdAt;
    protected Long updatedAt;

    // 생성자
    public BaseEntity() {
        this.id = UUID.randomUUID();  // 랜덤 UUID 객체 반환
        long now = System.currentTimeMillis();  // 현재
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Getter
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }  // 얘는 어디서 쓰일까?

    // update
    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}