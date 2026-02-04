package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new ArrayList<>();
    }

    // 읽음 상태 저장
    @Override
    public void save(ReadStatus readStatus) {
        data.removeIf(existReadStatus -> existReadStatus.getId().equals(readStatus.getId()));

        this.data.add(readStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return data.stream()
                .filter(status -> status.getId().equals(readStatusId))
                .findFirst();
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatus> findAll() {
        return data;
    }

    // 읽음 상태 삭제
    @Override
    public void delete(ReadStatus readStatus) {
        data.remove(readStatus);
    }

    // 유효성 검증 (중복 확인)
    @Override
    public Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.stream()
                .anyMatch(status -> status.getUserId().equals(userId) &&
                        status.getChannelId().equals(channelId));
    }
}