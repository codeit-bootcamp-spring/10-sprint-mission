package org.example.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private String nickname;
    private List<Channel> channels;


    private List<Message> messages;  // 👈 추가
    private Status status;

    public User (String username, String email, String password, String nickname) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.channels = new ArrayList<Channel>();
        this.messages = new ArrayList<Message>(); //참고로 타입 명시 안해도 됨
        this.status = Status.ACTIVE;
    }



    // Setters
    public void updateUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannels(List<Channel> channels) {
        this.channels = channels;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateMessages(List<Message> messages) {
        this.messages = messages;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    //getter
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Status getStatus() {
        return status;
    }
}
