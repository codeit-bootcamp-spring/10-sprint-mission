package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

// 사용자 별 마지막으로 확인된 접속시간을 표현하는 도메인(사용자의 온라인 상태를 확인하기 위해 활용)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private Instant lastActiveAt;

    // UserStatus는 User서비스를 통해 User가 만들어질 때 동시에 만들어져야함
    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }
    
    // 유저의 가장 최근 접속시간을 업데이트하기 위한 메소드
    public void updateLastActiveAt(Instant lastActiveAt) {
        if (lastActiveAt != null) {
            this.lastActiveAt = lastActiveAt;
        }
    }
    
    // 현재 로그인한 유저로 판단할 수 있는 메소드를 정의
    // 마지막 접속 시간이 현재시간으로 부터 5분 이내면 현재 접속 중인 유저로 간주
    public boolean isOnline() {
        Instant currentTime = Instant.now();
        Duration duration = Duration.between(this.lastActiveAt, currentTime);
        return duration.toMinutes()<5;
    }

    // User와 UserStatus 양방향 연관관계 편의 메소드
    public void setUser(User user) {
        this.user = user;
        if (user.getStatus()!=this) {
            user.setStatus(this);
        }
    }
}
