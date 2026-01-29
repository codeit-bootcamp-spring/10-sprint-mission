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
    private final List<UUID> messageIds = new ArrayList<>();
    private final List<UUID> joinedChannelIds = new ArrayList<>();
    private UUID profileId;

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

    public void updateProfileImage(UUID profileImage) {
        this.profileId = profileImage;
        update();
    }



}
