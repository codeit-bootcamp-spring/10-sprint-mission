package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "CHANNELS")
public class Channel extends BaseUpdatableEntity {

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType type;

  private String name;

  private String description;

  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(PublicChannelUpdateRequest request) {
    updateIfChanged(this.name, request.newName(), val -> this.name = val);
    updateIfChanged(this.description, request.newDescription(), val -> this.description = val);
  }
}
