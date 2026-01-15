package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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
        if (data.stream().anyMatch(channel -> channel.getChannelName().equals(channelName))) {
            throw new IllegalArgumentException("이미 등록된 계정입니다. " + channelName);
        }
        Channel channel = new Channel(channelName);
        data.add(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    public Channel findId(UUID channelID){
        return data.stream()
                .filter(user -> user.getId().equals(channelID))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public List<Channel> findAll(){
        return data;
    }


    // 해당 채널에서 작성된 메세지
    public List<Message> findMessages(UUID channelId){
        Channel channel = findId(channelId);
        return channel.getMessageList();
    }
    // 해당 채널의 유저
    public List<User> findUsers(UUID channelId){
        Channel channel = findId(channelId);
        return channel.getUserList();
    }


    public Channel update(UUID channelId, String channelName){
        Channel foundChannel = findId(channelId);

        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("이름이 비어있거나 공백입니다.");
        }
        foundChannel.setChannelName(channelName);
        return foundChannel;
    }

    public void delete(UUID channelId){
        Channel target = findId(channelId);
        data.remove(target);
    }



}
