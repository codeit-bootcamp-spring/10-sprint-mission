package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private  UserService userService;
    private  MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService){
        this.data = new HashMap<>();
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel joinUsers(UUID channelId, UUID ... userId) {
        Channel channel = findChannel(channelId);

        // 유저 추가
        Arrays.stream(userId)
                .map(userService::findUser)
                .forEach(user -> {
                    channel.addUsers(user.getId());
                    user.addChannel(channelId);
                });

        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        System.out.println("[채널 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        User user = userService.findUser(userId);
        List<Channel> channelList = new ArrayList<>();

        System.out.println("-- " + user + "가 속한 채널 조회 --");
        List<Channel> dataList = new ArrayList<>(data.values());
        dataList.stream()
                .filter(channel -> channel.getUserList().contains(user))
                .forEach(channel -> {channelList.add(channel);
                    System.out.println(channel);});

        return channelList;
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel channel = findChannel(channelId);
        channel.updateChannel(newName);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannel(channelId);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        List<UUID> userList = new ArrayList<>(channel.getUserList());
        userList.stream().map(userService::findUser).forEach(user -> user.getChannelList().remove(channelId));

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        List<UUID> messageList = new ArrayList<>(channel.getMessageList());
        messageList.stream().map(messageService::findMessage).forEach(message -> {
            messageService.delete(message.getId());
            // 유저에서도 메시지 삭제
            User author = userService.findUser(message.getUserId());
            author.getMessageList().remove(message.getId());
        });

        data.remove(channelId);
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }
}

