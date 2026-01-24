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
    public Set<Channel> fileLoad() {
        return channels;
    }

    @Override
    public void fileDelete(UUID id) {
        channels.removeIf(channel -> channel.getId().equals(id));
    }
}
