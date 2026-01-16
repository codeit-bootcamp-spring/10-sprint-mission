package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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
        if (data.stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 등록된 계정입니다. " + email);
        }
        User user = new User(userName, email);
        data.add(user);
        System.out.println(user.getUserName() + "님의 계정 생성이 완료되었습니다.");
        return user;
    }

    public User findById(UUID userId){
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + userId));
    }

    public User findByEmail(String email){
        return data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 이메일이 등록되어 있지 않습니다."));
    }


    public List<User> findAll(){
        return data;
    }

    public List<User> findUsersByChannel(UUID channelId){
        return data.stream()
                .filter(user -> user.getChannelList().stream().anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User update(UUID userId, String userName, String email) {
        User foundUser = findById(userId);
        Optional.ofNullable(userName)
                .filter(name -> !name.trim().isEmpty())
                .ifPresent(foundUser::setUserName);

        Optional.ofNullable(email)
                .filter(e -> !e.trim().isEmpty())
                .ifPresent(foundUser::setEmail);

        return foundUser;
    }


    public void addMessage(UUID userId, Message msg){
        User foundUser = findById(userId);
        foundUser.addMessage(msg);
    }

    public void addChannel(UUID userId, Channel channel){
        User foundUser = findById(userId);
        foundUser.addChannel(channel);
        channel.addUserList(foundUser);
    }


    public void delete(UUID userId){
        User target = findById(userId);
        data.remove(target);
    }

}
