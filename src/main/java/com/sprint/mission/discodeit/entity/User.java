package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    private String email;
    private String username;

    public User(String username, String email) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;

        this.username = username;
        this.email = email;
    }

//    public void update(UUID id,String username, String email) {
//        this.username = username;
//        this.id = UUID.randomUUID();
//        this.email = email;
//        this.updatedAt = System.currentTimeMillis();
//    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }


    public String getEmail() {
        return email;
    }
=======
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private List<Channel> myChannels = new ArrayList<>();
    private List<Message> myMessages = new ArrayList<>();
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f

    public String getUsername() {
        return username;
    }

<<<<<<< HEAD
    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
=======
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void updateUsername(String newUsername){
        this.username = newUsername;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public void updateEmail(String newEmail){
        this.email = newEmail;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public List<Message> getMyMessages() {
        return myMessages;
    }

    public List<Channel> getMyChannels() {
        return myChannels;
    }

    public void addMessage(Message message){
        this.myMessages.add(message);
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "이름: " + username + ", 이메일: " + email + ", 비밀번호: " + password;
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
    }
}
