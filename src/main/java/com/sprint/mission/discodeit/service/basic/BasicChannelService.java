package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.UserService;

import java.util.*;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private UserService userService;

    public BasicChannelService(ChannelRepository channelRepository){
        this.channelRepository = Objects.requireNonNull(channelRepository, "채널 저장소가 유효하지 않습니다.");
    }

    @Override
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService,"유저 서비스가 유효하지 않습니다.");
    }

    @Override
    public Channel createChannel(String name, String content, Channel.CHANNEL_TYPE type) {
        // 매개변수 검증.
        Objects.requireNonNull(name, "name은 null일수 없습니다.");
        Objects.requireNonNull(content, "content는 null일수 없습니다.");
        Objects.requireNonNull(type, "type은 null일수 없습니다.");

        // 같은 이름을 가진 채널이 존재하면 예외를 던짐 (중복 이름 불가)
        if(channelRepository.findAll().stream().anyMatch(c -> name.equals(c.getName()))){
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
        }

        // 입력받은 매개변수를 사용하여 채널 생성자 호출 (채널 객체 생성)
        Channel channel = new Channel(type, name, content);

        // ChannelRepository가 저장함.
        this.channelRepository.save(channel);

        return channel;
    }

    @Override
    public Channel readChannel(UUID uuid) {
        return channelRepository.findByID(uuid);
    }

    @Override
    public Channel updateChannel(UUID uuid, String name, String content, Channel.CHANNEL_TYPE type) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자 ID입니다.");

        Channel channel = readChannel(uuid);


        Optional.ofNullable(name)
                .ifPresent(channel::updateName);
        Optional.ofNullable(content)
                .ifPresent(channel::updateContent);
        Optional.ofNullable(type)
                .ifPresent(channel::updateType);

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 uuid 입니다.");

        Channel channel = readChannel(uuid);

        // 관계 정리(비즈니스 로직)
        channel.getUsers().clear();

        userService.readUsersbyChannel(uuid)
                .forEach(u -> {u.getChannelList().remove(channel);
                                    userService.save(u);});

        // 저장소에서 삭제
        channelRepository.delete(channel);
    }

    @Override
    public List<Channel> readAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> readChannelsbyUser(String userID) {
        User user = userService.readUser(userID);
        return user.getChannelList();
    }

    @Override
    public void userJoin(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);

        // 이미 유저가 해당 채널에 조인되어 있는가를 체크
        boolean alreadyJoined = user.getChannelList().stream()
                .anyMatch(c -> channelID.equals(c.getId()));

        // 이미 조인된 상태면 리턴.
        if (alreadyJoined) return;

        // 해당 유저의 채널 리스트에 해당 채널을 추가.
        user.getChannelList().add(channel);

        // 해당 채널의 유저 리스트에도 해당 유저를 추가.
        channel.getUsers().add(user);

        // user는 userService가 저장 -> userService 내부적으로 userRepo의 save 및 persist 동작
        userService.save(user);

        // channel은 repository로 저장
        channelRepository.save(channel);
    }

    @Override
    public void userLeave(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);

        // 해당 채널의 유저 리스트에 해당 유저가 존재한다면 유저 리스트에서 해당 유저를 삭제
        channel.getUsers().removeIf(u -> user.getId().equals(u.getId()));

        // 해당 유저의 채널 리스트에 해당 채널이 존재한다면 채널 리스트에서 해당 채널을 삭제
        user.getChannelList().removeIf(c -> channelID.equals(c.getId()));

        // 수정된 채널을 영속화
        channelRepository.save(channel);
        // 수정된 유저를 영속화
        userService.save(user);
    }

    @Override
    public void deleteChannelbyName(String name) {
        Objects.requireNonNull(name, "유효하지 않은 채널 이름입니다.");

        // 채널 이름으로 채널을 가져옴.
        Channel target = channelRepository.findAll().stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널"));

        UUID channelID = target.getId();

        // 유저 서비스를 통해 모든 유저의 채널 리스트를 뒤지고, 해당 이름을 가진 채널을 채널 리스트에서 삭제 및 유저 객체 영속화.
        userService.readAllUsers()
                        .forEach(u -> {u.getChannelList().removeIf(c -> channelID.equals(c.getId()));
                        userService.save(u);});

        // 해당 채널을 삭제 및 영속화
        channelRepository.delete(target);
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel);
    }
}
