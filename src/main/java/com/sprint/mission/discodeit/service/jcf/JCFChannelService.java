package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;


import java.util.*;

public class JCFChannelService implements ChannelService {
    private UserService userService;
    private ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository){
        this.userService = null;
        this.channelRepository = channelRepository;
    }


    // 상호작용할 유저 서비스를 주입합니다.
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // 채널 객체를 생성하고 data에 삽입합니다.
    @Override
    public Channel createChannel(String name, String content, Channel.CHANNEL_TYPE type) {
        // 매개변수 검증.
        Objects.requireNonNull(name, "name은 null일수 없습니다.");
        Objects.requireNonNull(content, "content는 null일수 없습니다.");
        Objects.requireNonNull(type, "type은 null일수 없습니다.");

        // 같은 이름을 가진 채널이 존재하면 예외를 던짐 (중복 이름 불가)
//        if(data.stream().anyMatch(c -> name.equals(c.getName()))){
//            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
//        }

        if(channelRepository
                .findAll()
                .stream()
                .anyMatch(c -> name.equals(c.getName())))
        {
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
        }

        // 입력받은 매개변수를 사용하여 채널 생성자 호출 (채널 객체 생성)
        Channel channel = new Channel(type, name, content);

        // 채널서비스 data 리스트에 추가.
        channelRepository.save(channel);

        return channel;
    }

    // 채널 UUID를 식별자로 사용하여 채널을 읽어오고 리턴합니다.
    @Override
    public Channel readChannel(UUID id){
        Objects.requireNonNull(id, "유효하지 않은 매개변수입니다.");

        return channelRepository.findByID(id);
    }

    // 채널 UUID를 식별자로 사용하여 해당 채널을 삭제합니다.
    @Override
    public void deleteChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "해당 매개변수가 유효하지 않습니다.");

        Channel channel = readChannel(uuid);

        // 채널<->유저 간 관계 해제
        channel.getUsers().forEach(u -> {
            u.getChannelList().remove(channel); // 유저의 채널 리스트에서 지우고자 하는 채널을 삭제한다.
            userService.save(u); // 수정된 유저를 영속화한다.
        });

        // 삭제하고자 하는 채널의 유저리스트를 초기화.
        channel.getUsers().clear();
        channelRepository.delete(channel);


    }

    // 유저ID를 식별자로 사용하여 채널을 검색하고 리턴합니다.
    public Set<Channel> readChannelsbyUser(String userID){
        Set<Channel> channelList = userService.readUser(userID).getChannelList();
        System.out.println(channelList);
        return channelList;
    }

    // 채널 UUID를 식별자로 사용하여 채널을 읽어오고, name과 content를 수정합니다.
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

        return channel;
    }

    // 채널과 유저 사이의 관계 성립 (참가)
    public void userJoin(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);
        user.getChannelList().add(channel);
        channel.getUsers().add(user);

    }

    // 채널과 유저 사이의 관계 파기 (탈퇴)
    @Override
    public void userLeave(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);
        channel.getUsers().remove(user);
        user.getChannelList().remove(channel);

    }

    @Override
    public void deleteChannelbyName(String name) {
        Objects.requireNonNull(name, "유효하지 않은 매개변수입니다.");


    }

    @Override
    public void save(Channel channel) {

    }

    // data 리스트 내 존재하는 모든 채널들을 리턴
    @Override
    public List<Channel> readAllChannels() {
//        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20));
//        data.forEach(System.out::println);
//        System.out.println("-".repeat(50));
        return channelRepository.findAll();
    }
}
