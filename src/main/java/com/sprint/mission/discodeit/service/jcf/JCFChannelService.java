package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        readChannel(id).updateChannelType(type);
        readChannel(id).updateChannelName(channelName);
        readChannel(id).updateChannelDescription(channelDescription);
    }

    @Override
    public void deleteChannel(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        list.remove(readChannel(id));
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
}
