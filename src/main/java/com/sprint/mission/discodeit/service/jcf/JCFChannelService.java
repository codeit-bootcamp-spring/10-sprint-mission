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
    public Channel create(String channelName, String type, UUID ownerId){
        User owner = userService.findById(ownerId);
        Channel channel = new Channel(channelName,type,owner);
        channel.getUserList().add(owner);//방장을 Channel에 넣기.
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

    public List<Channel> findByUser(UUID userId){ //특정유저가 참여하고 있는 채널
        User user = userService.findById(userId);
        return user.getChannelList();
    }

    public List<User> findByChannel(UUID channelId){ //특정 채널에 참여하고있는 유저 목록
        Channel channel = findById(channelId);
        return channel.getUserList();
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
    public void enter(UUID channelId,UUID userId){ //채널 id가 들어온다.
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        if(channel.getUserList().contains(user)){
            throw new IllegalArgumentException("이미 참여하고 있는 유저입니다.");
        }
        channel.getUserList().add(user);
        user.addChannel(channel);

    }

    @Override
    public void leave(UUID channelId,UUID userId){
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if(user.equals(channel.getOwner())){
            throw new IllegalArgumentException("방장은 퇴장할 수 없습니다.");
        }
        channel.getUserList().remove(user);
        user.removeChannel(channel);
    }

    public void removeUserFromAllChannel(UUID userId){
        if (userId == null){
            throw new IllegalArgumentException("삭제하려는 유저가 없습니다.");
        }
        //유저가 방장인경우 처리를 넣어줘야함.

        for(Channel channel : data.values()){
            channel.getUserList().removeIf(User -> User.getId().equals(userId));
        }
    }

}
