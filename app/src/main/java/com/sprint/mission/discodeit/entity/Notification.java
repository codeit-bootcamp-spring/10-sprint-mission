package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "notifications")
@Getter
public class Notification extends BaseEntity {

  @Column(nullable = false)
  private UUID receiverId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  public Notification(UUID receiverId, String title, String content) {
    super();
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }

  public boolean isOwnedBy(UUID userId) {
    return this.receiverId.equals(userId);
  }
}
