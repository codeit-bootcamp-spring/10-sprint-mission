package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.Validators;


import java.util.*;

public class JCFChannelService implements ChannelService {

    final ArrayList<Channel> list;

    public JCFChannelService() {
        this.list = new ArrayList<>();
    }
    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
            Validators.validationChannel(type, channelName, channelDescription);
            Channel channel = new Channel(type, channelName, channelDescription);
            list.add(channel);
            return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
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
    public Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        Validators.validationChannel(type, channelName, channelDescription);
        Channel channel = validateExistenceChannel(id);
        channel.updateChannelType(type);
        channel.updateChannelName(channelName);
        channel.updateChannelDescription(channelDescription);

        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        Channel channel = validateExistenceChannel(id);
        list.remove(channel);
    }

    @Override
    public boolean isChannelDeleted(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Channel channel : list) {
            if(id.equals(channel.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Channel validateExistenceChannel(UUID id) {
        Channel channel = readChannel(id);
        if(channel == null) {
            throw new NoSuchElementException("채널 id가 존재하지 않습니다.");
        }
        return channel;
    }
}
