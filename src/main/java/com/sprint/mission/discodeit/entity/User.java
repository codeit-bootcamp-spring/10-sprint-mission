package com.sprint.mission.discodeit.entity;


public class User extends BaseEntity{

    private  String name; // 사용자 닉네임
    private  String email; // 사용자 이메일

    // 이름과 이메일 받음 / 식별자 , 시간 생성
    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    // 업데이트 할 이름, 계정 / 시간 갱신
    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        touch();

    }

    // getter로 외부에서 값 확인 가능
    public String getUserName() {
        return name;
    }
    public String getUserEmail() {
        return email;
    }
    public String toString() {
        return "이름: " + name  + "\n" + "email: " + email;
    }
}

