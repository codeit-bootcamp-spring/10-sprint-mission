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
    public void CreateUser(User user){
        if(findId(user.getId()) != null){
            System.out.println("이미 있는 계정입니다.");
            return;
        }
        data.add(user);
        System.out.println(user.getUserName() + "님의 계정 생성이 완료되었습니다.");
    }

    public User findId(UUID id){
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }


    public List<User> findAll(){
        return data;
    }

    public void updateName(User user, String userName){
        if(findId(user.getId()) == null){
            System.out.println("존재하지 않는 계정입니다.");
            return;
        }
        user.setUserName(userName);
    }

    public void updateEmail(User user, String email){
        if(findId(user.getId()) == null){
            System.out.println("존재하지 않는 계정입니다.");
            return;
        }
        user.setEmail(email);
    }

    public void changeEmail(User user, String email){
        user.setEmail(email);
    }

    public void delete(UUID id){
        User target = findId(id);
        data.remove(target);
    }
}
