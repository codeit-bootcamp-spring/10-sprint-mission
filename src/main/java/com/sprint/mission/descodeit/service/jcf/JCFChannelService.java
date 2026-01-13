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
    private final UserService userService;
    private final MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService){
        this.data = new HashMap<>();
        this.userService = userService;
        this.messageService = messageService;
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
        for(UUID id : userId){
            User user = userService.findUser(id);
            channel.addUsers(user);
            user.addChannel(channel);
        }
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
        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        System.out.println();
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        User user = userService.findUser(userId);
        List<Channel> channelList = new ArrayList<>();

        System.out.println("-- " + user + "가 속한 채널 조회 --");

        for(Channel channel : new ArrayList<>(data.values())){
            if(channel.getUserList().contains(user)){
                channelList.add(channel);
                System.out.println(channel);
            }
        }
        return channelList;
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        Channel channel = data.get(channelId);

        System.out.println("-- " + channel + "에 속한 메시지 조회 --");
        for(Message message : channel.getMessageList()){
            System.out.println(message);
        }
        System.out.println();
        return channel.getMessageList();
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
        for(User user : channel.getUserList()){
            userService.findUser(user.getId()).getChannelList().remove(channel);
        }
        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        for(Message message : new ArrayList<>(channel.getMessageList())){
            messageService.delete(message.getId());
        }
        data.remove(channelId);
    }
}

