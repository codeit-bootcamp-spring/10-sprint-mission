package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Notification(UUID receiverId, String title, String content) {
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
    }
}
