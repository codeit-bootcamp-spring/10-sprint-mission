package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {

    private UUID id;
    private String userName;
    private String email;
    private String status;
    private Long createdAt;
    private Long updatedAt;

    public User(UUID id){
        this.id = id;

    }

    public User(String userName, String email, String status){
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }


    public String getUserName() {
        return userName;
    }

    //이름 변경
    public void setUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

//    public void setCreatedAt(Long createdAt) {
//        this.createdAt = createdAt;
//    }

    public Long getUpdatedAt() {

        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {

        this.updatedAt = updatedAt;
    }

    public String toString(){
        return "User{" +
                "id=" + id +
                ", username='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';

    }

}
