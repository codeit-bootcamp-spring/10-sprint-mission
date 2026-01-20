package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileChannelService implements ChannelService {
    private final FileBasicService<Channel> data;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        data = new FileBasicService<>("channels");
        this.userService = userService;
    }
    @Override
    public Channel addChannel(String name, String description, UUID ownerId, ChannelType channelType) {
        blockDirectChannel(channelType);
        validateChannelName(name);
        Channel channel = new Channel(name, description, userService.findUserById(ownerId), channelType);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel updateChannelInfo(UUID id, UUID ownerId, String name, String description) {
        Channel channel = getChannelById(id);
        blockDirectChannel(channel.getChannelType());
        User owner = userService.findUserById(ownerId);
        channel.checkChannelOwner(owner);
        if((name == null || name.equals(channel.getName())) && (description == null || description.equals(channel.getDescription()))) {
            throw new IllegalArgumentException("변경사항 없음");
        }
        Optional.ofNullable(name).
                filter(n -> !n.equals(channel.getName()))
                .ifPresent(n -> {if(channel.getChannelType()==ChannelType.PUBLIC){
                    validateChannelName(name);
                }
                    channel.setName(n);
                });
        Optional.ofNullable(description)
                .filter(d -> !d.equals(channel.getDescription()))
                .ifPresent(d -> channel.setDescription(d));
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data.values());//방어적 복사
    }

    @Override
    public void deleteChannelByIdAndOwnerId(UUID id, UUID ownerId) {
        Channel channel = getChannelById(id);
        blockDirectChannel(channel.getChannelType());
        User owner = userService.findUserById(ownerId);
        channel.checkChannelOwner(owner);
        channel.removeAllMembers();
        //채널 메세지 삭제 로직 필요
        data.remove(id);
    }

    //공개방 전용 유효성
    private void validateChannelName(String channelName) {
        boolean exists = data.values().stream()
                .filter(c->c.getChannelType()==ChannelType.PUBLIC)//공개방만
                .anyMatch(c ->c.getName().equals(channelName));
        if(exists){
            throw new IllegalArgumentException("이미 존재하는 공개 채널이름: "+channelName);
        }
    }

    private void validateChannel(UUID channelId) {
        boolean exists = data.containsKey(channelId);
        if(!exists){
            throw new NoSuchElementException("존재하지 않는 채널ID: "+channelId);
        }
    }

    public Channel getChannelById(UUID id) {
        validateChannel(id);
        return data.get(id);
    }

    public Channel createDirectChannel(List<UUID> chatterIdSet){
        Channel directChannel = new Channel(null,null,null,ChannelType.DIRECT);
        for(UUID chatterId : chatterIdSet){
            directChannel.addMember(userService.findUserById(chatterId));
        }
        data.put(directChannel.getId(), directChannel);
        return directChannel;
    }
    private void blockDirectChannel(ChannelType channelType) {
        if(channelType==ChannelType.DIRECT){
            throw new IllegalArgumentException("dm으로 접근할 수 없는 서비스");
        }
    }
}
