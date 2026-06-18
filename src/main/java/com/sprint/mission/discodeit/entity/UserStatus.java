//package com.sprint.mission.discodeit.entity;
//
//import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//
//import jakarta.persistence.Table;
//import java.time.Instant;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//@Table(name = "user_statuses")
//public class UserStatus extends BaseUpdatableEntity {
//
//  @OneToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "user_id", nullable = false)
//  private User user;
//
//  @Column(nullable = false)
//  private Instant lastActiveAt;
//
//  public static UserStatus create(User user, Instant lastActiveAt) {
//    return new UserStatus(user, lastActiveAt);
//  }
//
//  private UserStatus(User user, Instant lastActiveAt) {
//    if (user == null || lastActiveAt == null) {
//      throw new IllegalArgumentException("사용자 또는 마지막 활동 시간이 NULL임");
//    }
//    this.lastActiveAt = lastActiveAt;
//    setUser(user);
//  }
//
//  public void setUser(User user) {
//    this.user = user;
//    if (user.getUserStatus() == null || user.getUserStatus() != this) {
//      user.setUserStatus(this);
//    }
//  }
//
//  public boolean isOnline() {
//    Instant now = Instant.now();
//    Instant limitTime = lastActiveAt.plusSeconds(300);
//    return now.isBefore(limitTime);
//  }
//
//  public void update(Instant lastActiveAt) {
//    if (lastActiveAt == null) {
//      throw new IllegalArgumentException("마지막 활동 시간이 NULL임");
//    }
//    this.lastActiveAt = lastActiveAt;
//  }
//}
