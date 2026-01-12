package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel joinUsers(Channel channel, User... users) {
        if(channel == null){
            throw new NoSuchElementException();
        }

        // 유저 추가
        for(User user : users){
            channel.addUsers(user);
            user.addChannel(channel);
        }
        return channel;
    }

    @Override
    public Channel findCannel(UUID channelID) {
        Channel channel = data.get(channelID);
        if(channel == null){
            throw new NoSuchElementException();
        }
        System.out.println(channel);
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        System.out.println("[채널 전체 조회]");
        if(data.isEmpty()){
            System.out.println("조회할 채널이 없습니다");
            return new ArrayList<>();
        }

        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelID, String newName) {
        Channel channel = data.get(channelID);
        if(channel == null){
            throw new NoSuchElementException();
        }
        channel.updateChannel(newName);
        return channel;
    }

    @Override
    public boolean delete(UUID channelID) {
        Channel channel = data.get(channelID);

        if(channel == null){
            throw new NoSuchElementException();
        }

        MessageService messageService = new JCFMessageService();

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        for(Message message : channel.getMessageList()){
            messageService.delete(message.getId());
        }
        data.remove(channelID);

        return true;
    }
}

