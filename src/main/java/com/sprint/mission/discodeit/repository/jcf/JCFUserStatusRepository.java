package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatusEntity> data;

    public JCFUserStatusRepository() {
        this.data = new ArrayList<>();
    }

    // 사용자 상태 저장
    @Override
    public void save(UserStatusEntity userStatus) {
        data.removeIf(existUserStatus ->  existUserStatus.getId().equals(userStatus.getId()));

        data.add(userStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public Optional<UserStatusEntity> findById(UUID userStatusId) {
        return data.stream().
                filter(userStatus ->  userStatus.getId().equals(userStatusId))
                .findFirst();
    }

    // 특정 사용자 상태 단건 조회
    @Override
    public UserStatusEntity findByUserId(UUID userId) {
        return findAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));

    }

    // 사용자 전체 조회
    @Override
    public List<UserStatusEntity> findAll() {
        return data;
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UserStatusEntity userStatus) {
        data.remove(userStatus);
    }

    // 유효성 검증 (중복 확인)
    @Override
    public boolean existsById(UUID userId) {
        return data.stream()
                .anyMatch(userStatus ->  userStatus.getUserId().equals(userId));
    }
}
