package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    //사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인
    //사용자의 온라인 상태를 확인하기 위해 활용

    private UUID id;
    private Instant createdAt;//최초 로그인 시간
    private Instant updatedAt;
    //
    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        //
        this.userId = userId;

    }

    //사용자 온라인 상태 체크
    public boolean CheckOnline(){
        if(this.updatedAt == null){
            return false;
        }
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));//현재시각에서 5분을뺀 기준점
        return this.updatedAt.isAfter(fiveMinutesAgo);//마지막 접속 시간이 그 시간 이후면 true(온라인)

    }

    //마지막 접속 갱신
    public void touch(){
        this.updatedAt = Instant.now();
    }
}
