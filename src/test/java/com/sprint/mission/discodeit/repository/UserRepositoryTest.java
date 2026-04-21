package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 이름으로 검색하면 해당 유저 이름의 객체를 반환해야 한다.")
    void findByUsername_success() {
        // given
        String targetName = "김현재";
        User user = new User(targetName, "fred@naver.com", "123123");

        userRepository.save(user);
        // when
        Optional<User> foundUser = userRepository.findByUsername(targetName);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(targetName);
        assertThat(foundUser.get().getEmail()).isEqualTo("fred@naver.com");

    }

    @Test
    @DisplayName("이메일로 유저를 검색하면 존재하는 경우 true를 반환해야 한다")
    void existsByEmail_true() {
        // given
        String targetEmail = "fred@naver.com";
        User user = new User("김현재", targetEmail, "123123");
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(targetEmail);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이메일로 유저를 검색할 때 존재하지 않는 경우 false를 반환해야 한다")
    void existsByEmail_false() {
        // given
        String targetEmail = "sonata@naver.com";

        // when
        boolean result = userRepository.existsByEmail(targetEmail);

        // then
        assertThat(result).isFalse();
    }
}