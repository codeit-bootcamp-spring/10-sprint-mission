package org.example.entity;
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private List<Channel> channels;
    private Status status;

    public User (String username, String email, String password, String nickname) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.channels = new ArrayList<Channel>();
        this.status = Status.ACTIVE;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStatus(Status status) {
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
    public Status getStatus() {
        return status;
    }
}
