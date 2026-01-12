package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    // field
    private final List<UUID> channelIds = new ArrayList<>(); // 유저가 속한 채널 목록
    private String name;
    private String email;
    private String profileImageUrl;
    private String status;

    // constructor
    public User(String name, String email, String profileImageUrl, String status){
        super();
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
    }

    // Getter, update
    public List<UUID> getChannelIds() {return List.copyOf(this.channelIds);}
    public String getName() {return this.name;}
    public String getEmail() {return this.email;}
    public String getProfileImageUrl() {return this.profileImageUrl;}
    public String getStatus() {return this.status;}

    public void update(String name, String email, String profileImageUrl, String  status){
        updateTimestamp();
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
    }

    // channelId
    public void joinChannel(UUID channelId) {
        if (channelId == null)
            return;
        if (!this.channelIds.contains(channelId))
            this.channelIds.add(channelId);
    }

    public void leaveChannel(UUID channelId) {
        this.channelIds.remove(channelId);
    }
}
