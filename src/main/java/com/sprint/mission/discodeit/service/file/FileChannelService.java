package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileChannelService implements ChannelService {
    private final FileBasicService<User> userData;
    private final FileBasicService<Channel> channelData;
    private final FileBasicService<Message> messageData;
    private final UserService userService;

    public FileChannelService(UserService userService, FileBasicService<User> userData,FileBasicService<Channel> channelData,FileBasicService<Message> messageData) {
        this.channelData = channelData;
        this.userService = userService;
        this.userData = userData;
        this.messageData = messageData;
    }
    @Override
    public Channel addChannel(String name, String description, UUID ownerId, ChannelType channelType) {
        blockDirectChannel(channelType);
        validateChannelName(name);
        Channel channel = new Channel(name, description, userService.findUserById(ownerId), channelType);
        channelData.put(channel.getId(), channel);//오너는 단방향이기 때문에 영속화 필요 없음
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
        channelData.put(channel.getId(), channel);
        userData.saveAll();
        messageData.saveAll();
        return channel;
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channelData.values());//방어적 복사
    }

    @Override
    public void deleteChannelByIdAndOwnerId(UUID id, UUID ownerId) {
        Channel channel = getChannelById(id);
        blockDirectChannel(channel.getChannelType());
        User owner = userService.findUserById(ownerId);
        channel.checkChannelOwner(owner);
        channel.removeAllMembers();
        //채널 메세지 삭제 로직
        messageData.values()
                .stream()
                .filter(m->m.getChannel().getId().equals(id))
                .forEach(m->messageData.remove(m.getId()));//세이브도 자동됨
        userData.saveAll();
        channelData.remove(id);
    }

    //공개방 전용 유효성
    private void validateChannelName(String channelName) {
        boolean exists = channelData.values().stream()
                .filter(c->c.getChannelType()==ChannelType.PUBLIC)//공개방만
                .anyMatch(c ->c.getName().equals(channelName));
        if(exists){
            throw new IllegalArgumentException("이미 존재하는 공개 채널이름: "+channelName);
        }
    }

    private void validateChannel(UUID channelId) {
        boolean exists = channelData.containsKey(channelId);
        if(!exists){
            throw new NoSuchElementException("존재하지 않는 채널ID: "+channelId);
        }
    }

    public Channel getChannelById(UUID id) {
        validateChannel(id);
        return channelData.get(id);
    }

    public Channel createDirectChannel(List<UUID> chatterIdSet){
        Channel directChannel = new Channel(null,null,null,ChannelType.DIRECT);
        for(UUID chatterId : chatterIdSet){
            directChannel.addMember(userService.findUserById(chatterId));
        }
        channelData.put(directChannel.getId(), directChannel);
        userData.saveAll();//다이렉트 채널은 멤버가 처음부터 추가되니까 필요
        return directChannel;
    }
    private void blockDirectChannel(ChannelType channelType) {
        if(channelType==ChannelType.DIRECT){
            throw new IllegalArgumentException("dm으로 접근할 수 없는 서비스");
        }
    }
}
