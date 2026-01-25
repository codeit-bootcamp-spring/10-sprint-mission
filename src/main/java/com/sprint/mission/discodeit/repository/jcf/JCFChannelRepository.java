package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private static JCFChannelRepository instance = null;
    public static JCFChannelRepository getInstance(){
        if(instance == null){
            instance = new JCFChannelRepository();
        }
        return instance;
    }
    private JCFChannelRepository(){}

    private Set<Channel> channels = new HashSet<>();

    @Override
    public void fileSave(Set<Channel> channels) {
        this.channels.addAll(channels);
    }

    @Override
    public Set<Channel> fileLoadAll() {
        return channels;
    }

    @Override
    public Channel fileLoad(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public void fileDelete(UUID id) {
        channels.removeIf(channel -> channel.getId().equals(id));
    }
}
