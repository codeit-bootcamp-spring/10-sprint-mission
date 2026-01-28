package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;      // 모든 사용자

    public JCFUserRepository() {
        data = new ArrayList<>();
    }

    // 사용자 저장
    @Override
    public void save(User user) {
        data.removeIf(eexistUser -> eexistUser.getId().equals(user.getId()));

        data.add(user);
    }

    // 사용자 단건 조회
    @Override
    public Optional<User> findById(UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    // 사용자 전체 조회
    @Override
    public List<User> findAll() {
        return data;
    }

    // 사용자 삭제
    @Override
    public void delete(User targetUser) {
        data.remove(targetUser);
    }

    // 유효성 검증 (이메일 중복)
    @Override
    public boolean existsByEmail(String email) {
        return data.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    // 유효성 검증 (이름 중복)
    @Override
    public boolean existsByNickname(String nickname) {
        return data.stream().anyMatch(user -> user.getNickname().equals(nickname));
    }
}
