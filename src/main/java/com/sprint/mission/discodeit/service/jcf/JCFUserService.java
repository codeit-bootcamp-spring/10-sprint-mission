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

    public User findId(UUID userId){
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + userId));
    }

    public User findName(String name){
        return data.stream()
                .filter(user -> user.getUserName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 이메일이 등록되어 있지 않습니다."));
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

    public User updateName(UUID userId, String userName){
        User foundUser = findId(userId);
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("이름이 비어있거나 공백입니다.");
        }
        foundUser.setUserName(userName);
        return foundUser;
    }

    public User updateEmail(UUID userId, String email){
        User foundEmail = findId(userId);
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 비어있거나 공백입니다.");
        }
        foundEmail.setEmail(email);
        return foundEmail;
    }

    public void addMessage(UUID userId, Message msg){
        User foundUser = findId(userId);
        foundUser.addMessage(msg);
    }

    public void addChannel(UUID userId, Channel channel){
        User foundUser = findId(userId);
        foundUser.addChannel(channel);
        channel.addUserList(foundUser);
    }

    public List<Message> findMessages(UUID userId){
        User foundUser = findId(userId);
        return foundUser.getMessageList();
    }

    // 유저가 작성한 메세지 중 특정 채널에서의 메세지들
    public List<Message> findMessagesInChannel(UUID userId, UUID channelId){
        List<Message> messages = findMessages(userId);
        return messages.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }


    public List<Channel> findChannels(UUID userId){
        User user = findId(userId);
        return user.getChannelList();
    }

    public void delete(UUID userId){
        User target = findId(userId);
        data.remove(target);
    }

}
