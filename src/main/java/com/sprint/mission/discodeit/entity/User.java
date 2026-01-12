package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends Base {
    private String name;
    private Set<UUID> channelIDs;

    public User(String name) {
        super();
        this.name = name;
        this.channelIDs = new HashSet<UUID>();
    }

// Getter, Setter
    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public Set<UUID> getChannelIDs() {
        return channelIDs;
    }

    public void addJoinChannel(UUID channelID) {
        channelIDs.add(channelID);
    }

    public void removeChannel(UUID channelID){
        channelIDs.remove(channelID);
    }

}
