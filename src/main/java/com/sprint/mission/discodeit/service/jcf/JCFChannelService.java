package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    final ArrayList<Channel> list;

    public JCFChannelService() {
        this.list = new ArrayList<>();
    }
    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
            validationChannel(type, channelName, channelDescription);
            Channel channel = new Channel(type, channelName, channelDescription);
            list.add(channel);
            return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Channel channel : list) {
            if(id.equals(channel.getId())){
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> readAllChannel() {
        return list;
    }

    @Override
    public void updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        validationChannel(type, channelName, channelDescription);
        Channel channel = validateExistenceChannel(id);
        channel.updateChannelType(type);
        channel.updateChannelName(channelName);
        channel.updateChannelDescription(channelDescription);
    }

    @Override
    public void deleteChannel(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        Channel channel = validateExistenceChannel(id);
        list.remove(channel);
    }

    @Override
    public boolean isChannelDeleted(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Channel channel : list) {
            if(id.equals(channel.getId())) {
                return false;
            }
        }
        return true;
    }

    private void validationChannel(ChannelType type, String channelName, String channelDescription) {
        Objects.requireNonNull(type, "type은 null이 될 수 없습니다.");
        Objects.requireNonNull(channelName, "channelName은 null이 될 수 없습니다.");
        if(channelName.isBlank()) {
            throw new IllegalArgumentException("channelName에 공백을 입력할 수 없습니다.");
        }
        Objects.requireNonNull(channelDescription, "channelDescription은 null이 될 수 없습니다.");
        if(channelDescription.isBlank()) {
            throw new IllegalArgumentException("channelDescription에 공백을 입력할 수 없습니다.");
        }
    }

    private Channel validateExistenceChannel(UUID id) {
        Channel channel = readChannel(id);
        if(channel == null) {
            throw new NoSuchElementException("채널 id가 존재하지 않습니다.");
        }
        return channel;
    }
}
