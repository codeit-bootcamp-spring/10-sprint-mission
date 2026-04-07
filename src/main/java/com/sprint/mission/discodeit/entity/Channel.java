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
@Table(name="channels")
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ChannelType type; //channelType 의 enum 연결

  @Column(nullable = false)
  private String name;
  @Column(nullable = false, length = 500)
  private String description;

  public Channel(ChannelType type, String name, String description) {
//    this.id = UUID.randomUUID();
//    this.createdAt = Instant.now();
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }

  }
}
