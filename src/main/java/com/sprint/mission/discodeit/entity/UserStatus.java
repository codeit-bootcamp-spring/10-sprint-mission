package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// 사용자 별 마지막으로 확인된 접속시간을 표현하는 도메인(사용자의 온라인 상태를 확인하기 위해 활용)
// -> 유저의 최근 접속 시간을 필드로 가져야 하는지?
@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;// User의 id를 참조
    private Instant lastAccessTime;

    // UserStatus는 User서비스를 통해 User가 만들어질 때 동시에 만들어져야함
    public UserStatus(UUID userId, Instant lastAccessTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.lastAccessTime = lastAccessTime;
    }
    
    // 유저의 가장 최근 접속시간을 업데이트하기 위한 메소드
    public void updateLastAccessTime(Instant lastAccessTime) {
        if (lastAccessTime != null) {
            this.lastAccessTime = lastAccessTime;
            this.updatedAt = Instant.now();
        }
    }
    
    // 현재 로그인한 유저로 판단할 수 있는 메소드를 정의
    // 마지막 접속 시간이 현재시간으로 부터 5분 이내면 현재 접속 중인 유저로 간주
    public boolean isOnline() {
        Instant currentTime = Instant.now();
        Duration duration = Duration.between(this.lastAccessTime, currentTime);
        return duration.toMinutes()<5;
    }
}
