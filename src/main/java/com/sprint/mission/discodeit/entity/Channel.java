package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "CHANNELS")
@NoArgsConstructor
@Getter
public class Channel extends BaseUpdatableEntity {
    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;


    private List<UUID> userList;

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.userList = new ArrayList<>();
    }

     public void userJoin(UUID userID){
          Objects.requireNonNull(userID, "유효하지 않은 User 입니다.");
          if(userList.stream().anyMatch(userID::equals)){
              throw new IllegalStateException("이미 채널에 존재하는 User입니다.");
          }
          userList.add(userID);
     }

  public void userLeave(UUID userID){
    Objects.requireNonNull(userID, "유효하지 않은 User 입니다.");
    if(userList.stream().noneMatch(userID::equals)){
        throw new IllegalStateException("해당 채널에 유저가 존재하지 않습니다.");
    }
    userList.remove(userID);
  }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.setUpdatedAt(Instant.now());

        }
    }
}
