package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserEntity receiver;            // 수신인

    @Column(nullable = false)
    private String title;                   // 제목

    @Column(nullable = false)
    private String content;                 // 내용

    @Builder
    public NotificationEntity(UserEntity receiver, String title, String content) {
        this.receiver = receiver;
        this.title = title;
        this.content = content;
    }
}
