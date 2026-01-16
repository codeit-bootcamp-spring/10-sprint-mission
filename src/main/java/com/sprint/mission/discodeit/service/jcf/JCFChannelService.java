package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    public Channel createChannel(String channelName){
        if (data.stream().anyMatch(channel -> channel.getChannelName().equals(channelName))) {
            throw new IllegalArgumentException("같은 이름의 채널이 존재합니다. " + channelName);
        }
        Channel channel = new Channel(channelName);
        data.add(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    public Channel findId(UUID channelID){
        return data.stream()
                .filter(channel -> channel.getId().equals(channelID))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public Channel findName(String name){
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public List<Channel> findAll(){
        return data;
    }


    public List<Channel> findChannels(UUID userId){
        return data.stream()
                .filter(channel -> channel.getUserList().stream().anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    public Channel update(UUID channelId, String channelName){
        Channel foundChannel = findId(channelId);

        Optional.ofNullable(channelName)
                .filter(name -> !name.trim().isEmpty())
                .ifPresent(foundChannel::setChannelName);

        return foundChannel;
    }

    public void delete(UUID channelId){
        Channel target = findId(channelId);
        data.remove(target);
    }

}
