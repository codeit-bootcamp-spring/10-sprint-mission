package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends Base {
    private String name;
    private final Set<UUID> membersIDs;

    public Channel(String name) {
        super();
        this.name = name;
        this.membersIDs = new HashSet<UUID>();
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
    }


    public Set<UUID> getMembersIDs() {
        return membersIDs;
    }

    public void addMember(UUID memberID){
        membersIDs.add(memberID);
    }

    public void removeMembersIDs(UUID memberID) {
        membersIDs.remove(memberID);
    }
}
