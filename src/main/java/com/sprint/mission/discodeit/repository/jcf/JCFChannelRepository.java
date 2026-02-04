package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;
    private final ReadStatusRepository readStatusRepository;

    public JCFChannelRepository(ReadStatusRepository readStatusRepository) {
        this.readStatusRepository = readStatusRepository;
        data = new ConcurrentHashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    /*
    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }
    => 불변 리스트를 반환, 서비스 계층에서 리스트를 조작해야 할 때 문제가 될 수 있음
        - 일반적인 Repository 구현에서는 호출하는 쪽(Service)에서
        데이터를 가공하기 쉽도록 수정 가능한 리스트를 반환하는 것이 관례
     */

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsByName(String channelName) {
        return data.values().stream()
                .anyMatch(ch -> channelName.equals(ch.getName())); // 순서 변경 (NPE 방지)
    }

    @Override
    public Optional<Channel> findPrivateChannelByParticipants(Set<UUID> participantIds) {
        // 성능 최적화: 반복문 안에서 findAll()을 호출하지 않도록 미리 한 번만 조회
        List<ReadStatus> allReadStatuses = readStatusRepository.findAll();

        return data.values().stream()
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .filter(channel -> {
                    // 해당 채널의 참여자 목록 추출
                    Set<UUID> currentParticipants = allReadStatuses.stream()
                            .filter(rs -> rs.getChannelId().equals(channel.getId()))
                            .map(ReadStatus::getUserId)
                            .collect(Collectors.toSet());
                    // Set의 equals를 이용하여 구성원 일치 여부 확인 (순서 무관)
                    return currentParticipants.equals(participantIds);
                })
                .findFirst();
    }
}
