package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "channels")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Column(length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ChannelType type;

  public Channel(String name, String description, ChannelType type) {
    super();
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }
}
