package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.UserService;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.*;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private ChannelService channelService;

    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void setChannelService(ChannelService channelService){
        this.channelService = Objects.requireNonNull(channelService,"유효하지 않은 채널 서비스");
    }

    @Override
    public User createUser(String name, String email, String userId) {
        // 매개변수 유효성 체크
        Objects.requireNonNull(name, "유효하지 않은 이름입니다.");
        Objects.requireNonNull(email, "유효하지 않은 이메일입니다.");
        Objects.requireNonNull(userId, "유효하지 않은 ID입니다.");

        // 유저ID를 식별자로 삼아 중복 검증을 수행합니다.
        if (userRepository.findAll()
                .stream()
                .anyMatch(u -> userId.equals(u.getUserId()))){
            throw new IllegalStateException("이미 존재하는 유저입니다.");
        }

        // 유저 생성 및 data에 add.
        User user = new User(name, email, userId);
        userRepository.save(user);
        return user;
    }

    @Override
    public User readUser(String userId) {
        return userRepository.findByID(userId);
    }

    @Override
    public Set<User> readUsersbyChannel(UUID channelID) {
        return channelService.readChannel(channelID).getUsers();
    }

    @Override
    public User updateUser(String userId, String name, String email) {
        // 매개변수 유효성 검증
        Objects.requireNonNull(userId, "유효하지 않은 유저 아이디입니다.");

        // 찾고자 하는 유저를 유저ID를 통해 찾아냄.
        User user = readUser(userId);

        // name이 null이 아니라면 updateName을 호출
        // updateEmail이 null이 아니라면 updateEmail 호출
        Optional.ofNullable(name)
                .ifPresent(user::updateName);
        Optional.ofNullable(email)
                .ifPresent(user::updateEmail);

        userRepository.save(user);

        return user;
    }

    @Override
    public void deleteUser(String userID) {
        // 매개변수 유효성 검증
        Objects.requireNonNull(userID, "유효하지 않은 식별자입니다.");

        // 유저 읽어오기
        User user = readUser(userID);

        // 채널 서비스로부터 모든 채널을 읽어들이고, 해당 유저가 가입했다면 해당 유저를 채널의 유저리스트에서 삭제 및 유저 영속화
        channelService.readAllChannels()
                        .forEach(c -> {c.getUsers().removeIf(u -> userID.equals(u.getUserId()));
                        channelService.save(c);});

        userRepository.delete(user);
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void joinChannel(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        User user = readUser(userID);
        Channel channel = channelService.readChannel(channelID);

        // 이미 가입했으면 종료
        boolean already = user.getChannelList().stream().anyMatch(ch -> channelID.equals(ch.getId()));
        if (already) return;

        user.getChannelList().add(channel);
        channel.getUsers().add(user);

        userRepository.save(user);
        channelService.save(channel);
    }


    @Override
    public void exitChannel(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        User user = readUser(userID);
        Channel channel = channelService.readChannel(channelID);

        user.getChannelList().removeIf(ch -> channelID.equals(ch.getId()));
        channel.getUsers().removeIf(u -> userID.equals(u.getUserId()));

        userRepository.save(user);
        channelService.save(channel);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
