package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name="read_statuses")
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;

  // User 단방향 참조
  @ManyToOne(optional = false)
  @JoinColumn(name="user_id", nullable = false)
  private User user;
  // Channel 단방향 참조
  @ManyToOne(optional = false)
  @JoinColumn(name="channel_id", nullable = false)
  private Channel channel;

  @Column(name="last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
//    this.id = UUID.randomUUID();
//    this.createdAt = Instant.now();

    this.user = user;
    this.channel = channel;
    this.lastReadAt = (lastReadAt!=null)?lastReadAt:Instant.now();
  }

  public void update() {
    this.lastReadAt = Instant.now();
  }
}
