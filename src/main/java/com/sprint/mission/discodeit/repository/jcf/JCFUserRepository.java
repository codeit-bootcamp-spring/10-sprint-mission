package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
// name -> 확인할 설정 속성 이름
// havingValue -> 설정값이 이 값과 일치할 때만 Bean으로 등록
// matchIfMissing = true -> application.yaml에 해당 설정이 아예 없을 경우에도 이 클래스를 Bean으로 등록
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new ConcurrentHashMap<>();
        // 스프링은 기본적으로 싱글톤으로 빈을 관리한다.
        // 웹 환경에서 여러 요청이 동시에 들어올 경우 HashMap은 Thread-Safe 하지 않아 데이터가 꼬이거나 예외가 발생할 수 있음
        // 싱글톤이기 때문에 모든 사용자가 하나의 객체를 공유하게 되고, 그렇기 때문에 그 객체는 반드시 동시 접속을 처리할 수 있는 자료구조여야 한다
        // HashMap -> CocurrentHashMap 으로 대체
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
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

    public boolean existsByUsername(String username) {
        return data.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    public boolean existsByEmail(String email) {
        return data.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
