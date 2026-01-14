package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    public List<User> data = new ArrayList<>();
    ChannelService channelService;

    public JCFUserService(ChannelService channelService){
        this.channelService = channelService;
    }

    @Override
    public User createUser(String name, String email, String userId) {
        Objects.requireNonNull(name, "유효하지 않은 이름입니다.");
        Objects.requireNonNull(email, "유효하지 않은 이메일입니다.");
        Objects.requireNonNull(userId, "유효하지 않은 ID입니다.");

        if(data.stream()
                .anyMatch(u -> userId.equals(u.getUserId()))){
            throw new IllegalStateException("해당 ID는 이미 존재합니다.");
        }

        User user = new User(name, email, userId);
        data.add(user);
        return user;
    }

    @Override
    public User readUser(String userId) {
        Objects.requireNonNull(userId, "유효하지 않은 유저 ID입니다.");
        return data.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));

    }


    @Override
    public User updateUser(String userId, String name, String email) {
        Objects.requireNonNull(userId, "유효하지 않은 유저 아이디입니다.");

        if(data.stream()
                .anyMatch( u -> userId.equals(u.getUserId()))){
            throw new IllegalAccessError("해당 유저 ID가 중복됩니다.");
        }

        User user = readUser(userId);

        System.out.println(user);

        Optional.ofNullable(name)
                .ifPresent(user::updateName);
        Optional.ofNullable(email)
                .ifPresent(user::updateEmail);

        return user;
    }

    

    @Override
    public void deleteUser(String userID) {
        Objects.requireNonNull(userID, "유효하지 않은 식별자입니다.");

        User user = readUser(userID);

        user.getChannelList().forEach(t -> t.kickUser(user));
        data.remove(user);

    }


    @Override
    public ArrayList<User> readAllUsers() {
        return (ArrayList<User>) data;
    }

    public void joinChannel(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = channelService.readChannel(channelID);
        User user = readUser(userID);

        if(channel.getUsers().contains(user)){
            throw new IllegalStateException("채널에 이미 가입되어 있습니다.");
        }

        user.getChannelList().add(channel);
        channelService.userAdd(user, channel.getId());

    }

    public void exitChannel(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        User user = readUser(userID);
        Channel channel = channelService.readChannel(channelID);

        user.getChannelList().remove(channel); // 인스턴스의 channelList 필드에서 매개변수로 입력받은 채널을 제거합니다.
        channel.getUsers().remove(user); // 입력받은 채널의 userList에서도 연결을 끊습니다.

        System.out.printf("%s 님이 %s 채널에서 퇴장했습니다.%n", user.getUserId(), channel.getName());
    }


}