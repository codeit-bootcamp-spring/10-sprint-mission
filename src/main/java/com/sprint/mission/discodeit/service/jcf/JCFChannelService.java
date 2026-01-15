package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelList;

    public JCFChannelService(){
        this.channelList = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, String intro) {
        if(channelList.values().stream()
                .anyMatch(c -> c.getName().equals(name))){
            throw new IllegalArgumentException("이미 존재하는 채널명 입니다");
        }


        Channel newChannel = new Channel(name,intro);
        channelList.put(newChannel.getId(), newChannel);
        return newChannel;
    }


    @Override
    public Channel findChannelById(UUID channelId) {
        Channel channel = channelList.get(channelId);
        if(channel == null){
            throw new IllegalArgumentException("해당 채널을 찾을 수 없습니다.");
        }

        return channelList.get(channelId);
    }

    public List<Channel> findAllChannels () {return new ArrayList<>(channelList.values());}

    public void deleteChannel(UUID channelId){
        Channel channel = findChannelById(channelId);
        channelList.remove(channelId);
    }

    public Channel updateChannel(UUID channelId, String name, String intro) {
        Channel channel = findChannelById(channelId);

        Optional.ofNullable(name)
                .ifPresent(n-> {
                    if (channel.getName().equals(n)) {
                        throw new IllegalArgumentException("현재 사용 중인 채널 이름입니다");
                    }
                    channel.setName(n);
                });

        Optional.ofNullable(intro)
                .ifPresent(i -> {
                    if(channel.getIntro().equals(i)){
                        throw new IllegalArgumentException("현재 사용 중인 채널 설명입니다");
                    }
                    channel.setIntro(i);
                });

        channel.setUpdatedAt(System.currentTimeMillis());
        return channel;
    }

    @Override
    public void enter(Channel channel, User user) {
        channel.getUserList().add(user);
        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void exit(Channel channel, User user) {
        channel.getUserList().remove(user);
        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void addMessage(Channel channel, Message message) {
        channel.getMessageList().add(message);
        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void removeMessage(Channel channel, Message message) {
        channel.getMessageList().remove(message);
        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public int getCurrentUserCount(Channel channel) {
        return channel.getUserList().size();
    }

    @Override
    public int getMessageCount(Channel channel) {
        return channel.getMessageList().size();
    }


}

