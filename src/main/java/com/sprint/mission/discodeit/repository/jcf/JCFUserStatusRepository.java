package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new ArrayList<>();
    }

    // 사용자 상태 저장
    @Override
    public void save(UserStatus userStatus) {
        data.removeIf(existUserStatus ->  existUserStatus.getId().equals(userStatus.getId()));

        data.add(userStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return data.stream().
                filter(userStatus ->  userStatus.getId().equals(userStatusId))
                .findFirst();
    }

    // 사용자 전체 조회
    @Override
    public List<UserStatus> findAll() {
        return data;
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UserStatus userStatus) {
        data.remove(userStatus);
    }

    // 유효성 검증 (중복 확인)
    @Override
    public boolean existsById(UUID userId) {
        return data.stream()
                .anyMatch(userStatus ->  userStatus.getUserId().equals(userId));
    }
}
