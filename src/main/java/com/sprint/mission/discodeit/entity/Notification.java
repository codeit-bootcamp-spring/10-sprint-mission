package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "text", nullable = false)
  private String content;

  @Column(nullable = false)
  private UUID receiverId;

  public Notification(String title, String content, UUID receiverId) {
    this.title = title;
    this.content = content;
    this.receiverId = receiverId;
  }


}
