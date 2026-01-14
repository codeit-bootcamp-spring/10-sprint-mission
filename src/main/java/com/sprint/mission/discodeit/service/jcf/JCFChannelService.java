package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService){
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String channelName, String type, User owner){
        Channel channel = new Channel(channelName,type,owner);
        data.put(channel.getId(),channel); //data에 key value 값으로 넣음.
        return channel;
    }

    @Override
    public Channel findById(UUID id){
        if(data.get(id) == null){
            throw new IllegalArgumentException("채널이 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        channel.setChannelName(name);
        return channel;
    }

    @Override
    public Channel delete(UUID id) {
        Channel channel = findById(id);
        data.remove(id);
        return channel;
    }

    @Override
    public List<User> enter(UUID channelId,UUID userId){ //채널 id가 들어온다.
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.enter(user);
        user.addChannel(channel);

        return channel.getUserList();

    }

    @Override
    public List<User> leave(UUID channelId,UUID userId){
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        channel.leave(user);
        user.removeChannel(channel);

        return channel.getUserList();
    }

}
