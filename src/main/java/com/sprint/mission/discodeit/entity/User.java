package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.PipedReader;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends CommonEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String userName;
    private String userEmail;
    private String userPassword;
    private final List<Message> messages = new ArrayList<>();
    private final List<Channel> joinedChannels = new ArrayList<>();
    private final UUID profileId;

    public User(String userName, String userEmail, UUID profileId) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.profileId = profileId;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        update();
    }

    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
        update();
    }



}
