package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    public JCFChannelService() {
        this.data = new HashMap<>();
    } //인터페이스 객체 생성시 새로운 해쉬맵 할당

    @Override
    public void createChannel(Channel channel){
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel getChannelById(UUID uuid){
        Channel channel = data.get(uuid);
        if(channel == null){
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다: " + uuid);
        }
        return channel;
    }

    @Override
    public List<Channel> getChannelAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateChannel(Channel channel){
        Channel existing = data.get(channel.getId()); // 해당 아이디에 속한 channel 객체 불러옴.
        //만약 존재하는 채널이 없다면,
        if(existing == null) {
            throw new NoSuchElementException("수정할 채널이 없습니다: " + channel.getId());
        } existing.update(channel.getChannelName());
    }

    @Override
    public void deleteChannel(UUID uuid){
        if(!data.containsKey(uuid)){
            throw new NoSuchElementException("석제할 채널이 없습니다: " + uuid);
        }
        data.remove(uuid);
    }

    @Override
    public Channel getChannelByName(String channelName){
        for(Channel channel : data.values()){
            if(channel.getChannelName().equals(channelName)) return channel;
        }
        return null;
    }

    @Override
    public List<Message> getMessageInChannel(UUID uuid){
        Channel channel = data.get(uuid);
        if(channel == null) return Collections.emptyList();
        return channel.getMessages();
    }
}


