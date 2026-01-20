package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final MessageRepository messageRepository;
    public BasicChannelService(ChannelRepository channelRepository, UserService userService, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userService = userService;
        this.messageRepository = messageRepository;
    }
    @Override
    public Channel addChannel(String name, String description, UUID ownerId, ChannelType channelType) {
        blockDirectChannel(channelType);
        validateChannelName(name);
        Channel channel = new Channel(name, description, userService.findUserById(ownerId), channelType);
        channelRepository.save(channel);
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
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<Channel>(channelRepository.findAll());
    }

    @Override
    public Channel getChannelById(UUID id) {
        return channelRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("유효하지 않은 채널: "+id));
    }

    @Override
    public void deleteChannelByIdAndOwnerId(UUID id, UUID ownerId) {
        Channel channel = getChannelById(id);
        blockDirectChannel(channel.getChannelType());
        User owner = userService.findUserById(ownerId);
        channel.checkChannelOwner(owner);
        channel.removeAllMembers();
        //채널 메세지 삭제 로직 필요
        messageRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(id);
    }

    @Override
    public Channel createDirectChannel(List<UUID> chatterIdSet) {
        Channel directChannel = new Channel(null,null,null,ChannelType.DIRECT);
        for(UUID chatterId : chatterIdSet){
            directChannel.addMember(userService.findUserById(chatterId));
        }
        channelRepository.save(directChannel);
        return directChannel;
    }

    private void blockDirectChannel(ChannelType channelType) {
        if(channelType==ChannelType.DIRECT){
            throw new IllegalArgumentException("dm으로 접근할 수 없는 서비스");
        }
    }

    private void validateChannelName(String channelName) {
        boolean exists = channelRepository.findAllByChannelType(ChannelType.PUBLIC)
                .stream().anyMatch(c->c.getName().equals(channelName));
        if(exists){
            throw new IllegalArgumentException("이미 존재하는 공개 채널이름: "+channelName);
        }
    }
}
