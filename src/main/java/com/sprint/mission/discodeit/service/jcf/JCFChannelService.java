package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelList;
    private final UserService userService;

    public JCFChannelService(UserService userService)
    {   this.userService = userService;
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

        return channel;
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
    public void enter(UUID userId, UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);


        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void exit(UUID userId , UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);


        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void addMessage(UUID channelId, UUID messageId) {
        Channel channel = findChannelById(channelId);

        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void removeMessage(UUID channelId, UUID messageId) {
        Channel channel = findChannelById(channelId);

        channel.setUpdatedAt(System.currentTimeMillis());
    }

   @Override
    public int getCurrentUserCount(UUID channelId) {
        Channel channel = findChannelById(channelId);
        return channel.getUserList().size();
    }

    @Override
    public int getMessageCount(UUID channelId) {
        Channel channel = findChannelById(channelId);
        return channel.getMessageList().size();

    }
}

