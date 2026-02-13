package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChannelType type;
    private String name;
    private String description;
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
            throw new IllegalStateException("이미 유저가 채널에 가입되어 있습니다.");
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
