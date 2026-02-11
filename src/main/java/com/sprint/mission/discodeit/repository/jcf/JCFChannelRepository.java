package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void save(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel은 null일 수 없습니다.");
        }
        if (channel.getId() == null) {
            throw new IllegalArgumentException("channel.id는 null일 수 없습니다.");
        }
        data.put(channel.getId(), channel);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        data.remove(id);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }


}
