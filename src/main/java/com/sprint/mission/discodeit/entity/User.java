package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String email;
    private final LocalDate birthDate; // 수정불가능
    private String phoneNumber;
    private transient String password;


    public User(String name, String email, LocalDate birthDate , String phoneNumber, String password){
        super();
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
        setUpdatedAt(System.currentTimeMillis());
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String userEmail){
        this.email = userEmail;
        setUpdatedAt(System.currentTimeMillis());
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        setUpdatedAt(System.currentTimeMillis());
    }
    public String getPassword(){
        return password;
    }

    public void setPassword (String userPassword) {
        this.password = userPassword;
        setUpdatedAt(System.currentTimeMillis());
    }
}
