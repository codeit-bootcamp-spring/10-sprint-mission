package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends Common{

//    private UUID id;
    private String userName;
    private String email;
    private String status;
//    private Long createdAt;
//    private Long updatedAt;


    public User(String userName, String email, String status){
        //super가 숨겨져있음.
        this.userName = userName;
        this.email = email;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    //이름 변경
    public void setUserName(String userName) {
        this.userName = userName;
        setUpdatedAt();
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

    @Override
    public String toString() {
        return String.format(
                "User{id=%s, username='%s', email='%s', status='%s', 생성일자=%d, 수정일자=%d}",
                getId(),
                userName,
                email,
                status,
                getCreatedAt(),
                getUpdatedAt()
        );
    }


}
