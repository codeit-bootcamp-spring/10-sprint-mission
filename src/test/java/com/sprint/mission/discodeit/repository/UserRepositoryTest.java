package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.Role;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureMockMvc(addFilters = false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("전체 사용자 조회 성공 - status, profile fetch join")
    void findAll_success() {
        // given
        User user1 = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        User user2 = new User("달룡", "dalyong@naver.com", "ekffyd123", null, Role.USER);
        userRepository.save(user1);
        userRepository.save(user2);
        // when
        List<User> result = userRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("username")
                .containsExactlyInAnyOrder("달선", "달룡");
    }

    @Test
    @DisplayName("전체 사용자 조회 성공 - 사용자 없음")
    void findAll_empty() {
        // when
        List<User> result = userRepository.findAll();

        // then
        assertThat(result).isEmpty();
    }
}
