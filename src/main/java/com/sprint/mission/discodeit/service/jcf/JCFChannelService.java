package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    public void createChannel(Channel channel){
        if(findId(channel.getId()) != null){
            System.out.println("이미 있는 채널입니다.");
            return;
        }
        data.add(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
    }

    public Channel findId(UUID id){
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Channel> findAll(){
        return data;
    }

    public void update(Channel channel, String channelName){
        if(findId(channel.getId()) == null){
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }
        channel.setChannelName(channelName);
    }

    public void delete(UUID id){
        Channel target = findId(id);
        data.remove(target);
    }
}
