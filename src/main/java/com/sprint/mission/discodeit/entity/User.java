package com.sprint.mission.discodeit.entity;

import java.time.LocalDate;
import java.util.UUID;

public class User extends BaseEntity {
    private String name;
    private String email;
    private final LocalDate birthDate; // 수정불가능
    private String phoneNumber;
    private String password;


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

    public void setInfo(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        setUpdatedAt(System.currentTimeMillis());
    }
}
