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
    // UserStatus가 User를 fk(user_id)로 참조하기 때문에 연관관계의 주인은 UserStatus, UserStatus의 user를 통해서 관리
    // 그러므로 UserStatus에 연관관계 편의 메서드를 작성
    // 서비스(외부)에서 사용할 때는 이 메서드 사용을 위해 public으로
    // 내부 User의 setStatus()는 default로 두어서 UserStaus의 setUser() 사용을 강제하게끔 했음
    public void setUser(User user) {
        this.user = user;
        user.setStatus(this);
    }
}
