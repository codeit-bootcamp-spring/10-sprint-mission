package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    public Channel createChannel(String channelName){
        boolean isExist = data.stream()
                .anyMatch(channel -> channel.getChannelName().equals(channelName));
        if (isExist) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + channelName);
        }
        Channel channel = new Channel(channelName);
        data.add(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    public Channel findId(Channel channel){
        return data.stream()
                .filter(user -> user.getId().equals(channel.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public List<Channel> findAll(){
        return data;
    }


    public Channel update(Channel channel, String channelName){
        Channel foundChannel = findId(channel);
        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("이름이 비어있거나 공백입니다.");
        }
        channel.setChannelName(channelName);
        return channel;
    }

    public void delete(Channel channel){
        Channel target = findId(channel);
        data.remove(target);
    }
}
