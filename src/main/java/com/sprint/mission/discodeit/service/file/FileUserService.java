package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final FileBasicService<User> userData;
    private final FileBasicService<Channel> channelData;
    private final FileBasicService<Message> messageData;
    public FileUserService(FileBasicService<User> userData, FileBasicService<Channel> channelData, FileBasicService<Message> messageData) {
        this.userData = userData;
        this.channelData = channelData;
        this.messageData = messageData;
    }

    @Override
    public User signUp(String userName,String email, String password) {
        validateEmail(email);
        User user = new User(userName, email, password);
        userData.put(user.getId(), user);
        return user;
    }
    @Override
    public User signIn(String email, String password) {
        User user = userData.values().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password)).findFirst().orElse(null);
        if(user == null){
            throw new NoSuchElementException("유효하지 않은 이메일 또는 비밀번호");
        }
        return user;
    }
    @Override
    public User updateInfo(UUID id, String userName, String email, String password) {
        User user = findUserById(id);
        //요청한 값이 널 또는 이전과 같은 값들로만 구성된 경우
        boolean unChanged = (userName ==null || userName.equals(user.getUserName()))
                && (email ==null || email.equals(user.getEmail()))
                && (password ==null || password.equals(user.getPassword()));
        if(unChanged){
            throw new IllegalArgumentException("변경사항 없음");
        }
        Optional.ofNullable(userName)
                .filter(n -> !n.equals(user.getUserName()))
                .ifPresent(n->user.setUserName(n));
        Optional.ofNullable(email)
                .filter(n -> !n.equals(user.getEmail()))
                .ifPresent(e ->{
                    validateEmail(email);
                    user.setEmail(e);
                });
        Optional.ofNullable(password)
                .filter(n -> !n.equals(user.getPassword()))
                .ifPresent(p -> user.setPassword(p));
        userData.put(user.getId(), user);
        channelData.saveAll();
        messageData.saveAll();
        return user;
    }

    @Override
    public User findUserById(UUID id) {
        validateUser(id);
        return userData.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>(userData.values());
        return users;
    }

    @Override
    public void removeUserById(UUID id) {
        User user = findUserById(id);
        user.removeAllChannels();
        user.setUserName("[삭제된 사용자]");
        channelData.saveAll();
        messageData.saveAll();
        //소유 채널 삭제? 선택사항
        userData.remove(id);
    }

    private void validateEmail(String email) {
        boolean exists = userData.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 이메일: "+email);
        }
    }

    private void validateUser(UUID id) {
        boolean exists = userData.containsKey(id);
        if (!exists) {
            throw new NoSuchElementException("유효하지 않은 사용자ID: "+id);
        }
    }

}
