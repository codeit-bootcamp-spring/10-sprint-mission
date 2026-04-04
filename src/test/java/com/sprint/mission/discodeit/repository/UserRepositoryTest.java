package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이름으로 유저 존재 유무 확인 - 성공")
    void existsByUsername_success() {
        // given
        userRepository.save(new User("alice", "alice@test.com", "pw", null));

        // when + then
        assertThat(userRepository.existsByUsername("alice")).isTrue();
    }

    @Test
    @DisplayName("이름으로 유저 존재 유무 확인 - 실패 (없는 이름)")
    void existsByUsername_fail() {
        // given
        userRepository.save(new User("alice", "alice@test.com", "pw", null));

        // when + then
        assertThat(userRepository.existsByUsername("bob")).isFalse();
    }

    @Test
    @DisplayName("유저 이름과 비밀번호로 조회 - 성공")
    void findByUsernameAndPassword_success() {
        // given
        userRepository.save(new User("alice", "alice@test.com", "pw123", null));

        // when + then
        assertThat(userRepository.findByUsernameAndPassword("alice", "pw123")).isPresent();
    }

    @Test
    @DisplayName("유저 이름과 비밀번호로 조회 - 실패")
    void findByUsernameAndPassword_fail() {
        // given
        userRepository.save(new User("alice", "alice@test.com", "pw123", null));

        // when + then
        // pw123 != wrong -> 빈 리스트를 반환함.
        assertThat(userRepository.findByUsernameAndPassword("alice", "wrong")).isEmpty();
    }

    @Test
    @DisplayName("유저 이름 중복 검증 - 성공")
    void existsByUsernameAndIdNot_success() {
        // given
        // alice 유저 생성
        User saved = userRepository.save(new User("alice", "alice@test.com", "pw", null));

        // when + then
        // existsByUsernameAndIdNot -> 유저 이름이 존재하면서 Id가 존재하지 않으면 true

        // alice라는 이름이 존재하지만 randomUUID는 존재 X -> true 반환
        assertThat(userRepository.existsByUsernameAndIdNot("alice", UUID.randomUUID())).isTrue();
        // alice라는 이름이 존재하고 randomUUID 존재 -> false 반환
        assertThat(userRepository.existsByUsernameAndIdNot("alice", saved.getId())).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 확인 메서드 - 성공")
    void existsByEmailAndIdNot_success() {
        // given
        // 유저 생성 및 영속화
        User saved = userRepository.save(new User("alice", "alice@test.com", "pw", null));

        // existsByEmailAndIdNot -> ID가 같지 않으면서 이메일이 중복되는지 확인하는 메서드
        // when + then

        // alice@test.com 이라는 이메일은 존재하면서 randomId는 존재하지 않음 -> true 반환
        assertThat(
            userRepository.existsByEmailAndIdNot("alice@test.com", UUID.randomUUID())).isTrue();
        // alice@test.com 이메일이 존재하면서 alice의 id도 존재 -> false 반환
        assertThat(userRepository.existsByEmailAndIdNot("alice@test.com", saved.getId())).isFalse();
    }
}
