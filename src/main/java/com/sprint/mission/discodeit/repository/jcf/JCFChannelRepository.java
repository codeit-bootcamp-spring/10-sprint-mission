package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("jcf")
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel){
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    // PUBLIC 채널 전부 조회
    @Override
    public List<Channel> findAllPublic() {
        return data.values().stream()
                .filter(Channel::isPublic)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
