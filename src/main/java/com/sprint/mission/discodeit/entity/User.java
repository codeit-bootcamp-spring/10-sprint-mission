package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class User{

    private final UUID userId; // 유저 식별자 -> 변경 불가능하게 생성
    private  String name; // 사용자 닉네임
    private  String email; // 사용자 이메일
    private final Long userCreatedAt; // 계정생성 시점
    private  Long userUpdatedAt; // 업데이트 시점

    // 메인 클래스에서 이름과 이메일 받음 / 랜덤 식별자(중복없음) , 계정생성 타임스템프 생성
    public User(String name, String email) {
        this.userId = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.userCreatedAt = System.currentTimeMillis();
    }

    // 업데이트 할 이름 계정 / 업데이트 타임스템프만 갱신
    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        this.userUpdatedAt = System.currentTimeMillis();

    }

    // getter로 외부에서 값 확인 가능
    public UUID getUserId() {
        return userId;
    }
    public String getUserName() {
        return name;
    }
    public String getUserEmail() {
        return email;
    }
    public String toString() {
        return "{id : " + userId + ", name: " + name + ", email: " + email + "}";
    }
    public long getUserCreatedAt() {
        return userCreatedAt;
    }
    public long getUserUpdatedAt() {
        return userUpdatedAt;
    }



}

