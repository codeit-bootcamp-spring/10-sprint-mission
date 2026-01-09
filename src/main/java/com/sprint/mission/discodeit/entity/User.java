package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends BaseEntity {
    // field
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
}
