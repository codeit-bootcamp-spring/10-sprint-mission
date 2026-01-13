package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {

    // 유저가 속한 채널 목록
    private final List<UUID> channelIds = new ArrayList<>();

    private String name;
    private String email;
    private String profileImageUrl;

    // constructor
    public User(String name, String email, String profileImageUrl){
        super();
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    // Getter, update
    public List<UUID> getChannelIds() {return List.copyOf(this.channelIds);}
    public String getName() {return this.name;}
    public String getEmail() {return this.email;}
    public String getProfileImageUrl() {return this.profileImageUrl;}

    public void update(String name, String email, String profileImageUrl){
        updateTimestamp();
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    // 채널 참여 시 채널 목록에 채널 추가
    public void joinChannel(UUID channelId) {
        if (channelId == null)
            return;
        if (!this.channelIds.contains(channelId))
            this.channelIds.add(channelId);
    }
    // 퇴장 시 삭제
    public void leaveChannel(UUID channelId) {
        this.channelIds.remove(channelId);
    }
}
