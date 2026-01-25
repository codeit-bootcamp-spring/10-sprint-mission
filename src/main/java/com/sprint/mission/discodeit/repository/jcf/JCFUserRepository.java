package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository(){
        data = new ArrayList<>();
    }
    @Override
    public void save(User user) {
        data.add(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + id)));
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public void delete(UUID id) {
        Optional<User> target = findById(id);
        data.remove(target);
    }

    @Override
    public void deleteAll() {
        data.clear(); // 리스트 내부의 모든 채널 객체 삭제
        System.out.println("JCF: 모든 채널 데이터를 초기화했습니다.");
    }
}
