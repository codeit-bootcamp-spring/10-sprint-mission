package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;

    public JCFChannelRepository() {
        data = new ArrayList<>();
    }

    // 채널 저장
    @Override
    public void save(Channel channel) {
        data.removeIf(existChannel -> existChannel.getId().equals(channel.getId()));

        data.add(channel);
    }

    // 채널 단건 조회
    @Override
    public Optional<Channel> findById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst();
    }

    // 채널 전체 조회
    @Override
    public List<Channel> findAll() {
        return data;
    }

    // 채널 삭제
    @Override
    public void delete(Channel channel) {
        data.remove(channel);
    }
}
