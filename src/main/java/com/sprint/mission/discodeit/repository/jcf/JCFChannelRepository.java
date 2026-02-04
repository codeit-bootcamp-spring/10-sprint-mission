package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFChannelRepository implements ChannelRepository {
    final ArrayList<Channel> data;

    public JCFChannelRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(channel.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), channel);
        } else {
            data.add(channel);
        }

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    } // 저장 로직


    @Override
    public void deleteById(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
