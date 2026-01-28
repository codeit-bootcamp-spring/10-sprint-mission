package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserRepository {

    //"저장 로직" 과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
    User save(User user); //create와 update를 save로 통합
    User findById(UUID userId);
    List<User> findAll();
    void delete(UUID userId);

}
