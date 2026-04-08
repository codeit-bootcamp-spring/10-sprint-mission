package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(new User("tester", "test@example.com","asdf1234"));
    }

    @Test
    @DisplayName("유저를 username으로 조회할 수 있다.")
    void find_by_username_success(){
        // when
        Optional<User> result = userRepository.findByUsername("tester");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("tester");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("알맞는 username이 존재하지 않으면 조회 시 빈 optional을 반환한다.")
    void find_by_username_empty(){
        // when
        Optional<User> foundUser = userRepository.findByUsername("jiho");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail - [성공] 존재하는 email로 검색 시 true를 반환한다.")
    void exists_by_email_success() {
        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - [실패] 존재하지 않는 email로 검색 시 false를 반환한다.")
    void exists_by_email_fail() {
        // when
        boolean exists = userRepository.existsByEmail("jiho@example.com");

        // then
        assertThat(exists).isFalse();
    }

}
