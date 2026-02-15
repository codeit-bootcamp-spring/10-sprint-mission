package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "jcf" ,
        matchIfMissing = true
)
public class JCFChannelRepository implements ChannelRepository {
    private final List<ChannelEntity> data;

    public JCFChannelRepository() {
        data = new ArrayList<>();
    }

    // 채널 저장
    @Override
    public void save(ChannelEntity channel) {
        data.removeIf(existChannel -> existChannel.getId().equals(channel.getId()));

        data.add(channel);
    }

    // 채널 단건 조회
    @Override
    public Optional<ChannelEntity> findById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst();
    }

    // 채널 전체 조회
    @Override
    public List<ChannelEntity> findAll() {
        return data;
    }

    // 채널 삭제
    @Override
    public void delete(ChannelEntity channel) {
        data.remove(channel);
    }
}
