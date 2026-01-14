package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    //data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드
    public User CreateUser(String userName, String email){
        boolean isExist = data.stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isExist) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + email);
        }
        User user = new User(userName, email);
        data.add(user);
        System.out.println(user.getUserName() + "님의 계정 생성이 완료되었습니다.");
        return user;
    }

    public User findId(UUID id){
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + id));
    }

    public User findEmail(String email){
        return data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 이메일이 등록되어 있지 않습니다."));
    }


    public List<User> findAll(){
        return data;
    }

    public User updateName(User user, String userName){
        User foundUser = findId(user.getId());
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("이름이 비어있거나 공백입니다.");
        }
        foundUser.setUserName(userName);
        return foundUser;
    }

    public User updateEmail(User user, String email){
        User foundEmail = findEmail(user.getEmail());
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 비어있거나 공백입니다.");
        }
        foundEmail.setUserName(email);
        return foundEmail;
    }

    public void delete(User user){
        User target = findId(user.getId());
        data.remove(target);
    }



    // 메세지 가져오기
    // 메세지 추가
    // 메세지 삭제
    // 메세지 전체삭제

    // 채널도 위와 마찬가지

}
