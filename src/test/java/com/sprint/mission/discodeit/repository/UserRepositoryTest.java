package com.sprint.mission.discodeit.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("성공: 사용자 이름으로 사용자 조회 (EntityGraph 작동 확인)")
    void findByUsername_Success() {
        // TODO: 사용자 및 관련 엔티티(Status, Profile) 저장 후 조회 시 Fetch Join이 잘 되는지 확인
    }

    @Test
    @DisplayName("실패: 존재하지 않는 사용자 이름으로 조회 시 Optional.empty() 반환")
    void findByUsername_Fail_NotFound() {
        // TODO: 존재하지 않는 이름으로 조회 시 결과가 비어있는지 확인
    }

    @Test
    @DisplayName("성공: 이메일 중복 여부 확인")
    void existsByEmail_Success() {
        // TODO: 동일한 이메일이 있을 때 true 반환 확인
    }

    @Test
    @DisplayName("실패: 등록되지 않은 이메일의 경우 false 반환")
    void existsByEmail_Fail_NotExists() {
        // TODO: 존재하지 않는 이메일 확인
    }
}
