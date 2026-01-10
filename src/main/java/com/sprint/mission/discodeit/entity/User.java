package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Basic {
    private String userName;
    private String userEmail;


    //내가 작성한 메세지 리스트.....추가!!!?? 유지가 메세지를 작성하면.... -> 메세지에 리스트항목에도 동시에 추가되어야한다...
    private List<Message> messages = new ArrayList<>();

    public String alias; //닉네임


// user 생성자
    public User(String userName, String userEmail, String alias){
        super(); // user 만드는 메소드 ... -> ID 와 CreatedAt 할당.
        this.userName = userName;
        this.userEmail = userEmail;
        this.alias = alias;
    }

    //  멤버 변수들 getter setter 설정.
    public String getUserName(){
        return userName;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public String getAlias(){
        return alias;
    }
    public List<Message> getMessage() {return messages;}

    // User 정보 변경 그대로인건 null 로 채우기.
    public void update(String username, String email, String alias) {
        if (username != null) this.userName = username;
        if (email != null) this.userEmail = email;
        if (alias != null) this.alias = alias;
        update();
    }
    // 메세지 관련!!!!
    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        // 중복 방지
        if (!messages.contains(message)) {
            messages.add(message); //list 에 mesaage add하고,
            message.setSender(this); //
        }
    }

    @Override
    public String toString() {
        return userName;
    }

}
