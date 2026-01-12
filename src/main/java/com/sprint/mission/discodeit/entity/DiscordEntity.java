package com.sprint.mission.discodeit.entity;
import java.util.UUID; // UUID 타입을 사용하기 위해 임포트


public abstract class DiscordEntity {
    private final UUID id;
    private final Long createdAt;
    protected Long updatedAt;


    public DiscordEntity(){
        this.id = UUID.randomUUID(); // 랜덤한 값의 UUID 값 생성 및 초기화
        this.createdAt = System.currentTimeMillis() / 1000; // 초 단위로 변환합니다.
        updateTime();
    }

    public UUID getId() {
            return this.id;

    }

    public Long getCreatedAt(){
        return this.createdAt;
    }

    public Long getUpdatedAt(){
        return this.updatedAt;
    }

    public void updateTime(){
        this.updatedAt = System.currentTimeMillis() / 1000;
    }



}
