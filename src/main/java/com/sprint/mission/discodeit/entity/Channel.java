package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;

@Getter
public class Channel extends DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private ChannelType type;
    private String name = null;
    private String description = null;
    private List<UUID> joinedUser = null;

    public Channel(ChannelType type, String name, String description) {
        super();
        //
        this.type = type;

        if(type == PRIVATE){
            joinedUser = new ArrayList<>();
        }
        else{
            this.name = name;
            this.description = description;
        }

    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if(type==PRIVATE) throw new IllegalStateException("Private Channel cannot be updated");

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void join(UUID userId){
        joinedUser.add(userId);
    }

    public void leave(UUID userId){
        if(!joinedUser.contains(userId)) joinedUser.remove(userId);
    }
}
