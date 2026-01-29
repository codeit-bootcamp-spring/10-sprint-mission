package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity{
    private String name;
    private String email;
    // 프로필 이미지로 BinaryContent를 가져옴
    private UUID profileImageId;
    private String password;
    private final List<UUID> messageList;
    private final List<UUID> channelList;
    private final List<UUID> friendsList;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
        this.friendsList = new ArrayList<>();
    }

    public void addProfileImage(UUID binaryContentId){
        this.profileImageId = binaryContentId;
    }

    public void addChannel(UUID channdId){
        if(!this.getChannelList().contains(channdId)){
            this.getChannelList().add(channdId);
        }
    }

    public void addMessage(UUID messaageId){
        if(!this.getMessageList().contains(messaageId)){
            this.getMessageList().add(messaageId);
        }
    }

    public void addFriend(UUID userId){
        if(!this.getFriendsList().contains(userId)){
            this.getFriendsList().add(userId);
        }
    }

    public void updateName(String name){this.name = name;}

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateProfileImg(UUID profileImageId){this.profileImageId = profileImageId;}

    public void updatePassword(String password){this.password = password}

    @Override
    public String toString() {
        return name;
    }
}
