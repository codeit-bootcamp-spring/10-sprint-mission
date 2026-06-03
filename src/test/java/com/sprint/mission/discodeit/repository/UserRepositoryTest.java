package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(AppConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    /*
        단건 조회
     */
    // [성공]
    @Test
    @DisplayName("사용자 이름을 이용한 단건 조회 성공")
    void find_by_username_success() {
        // given
        String username = "yushi";

        // 조회할 사용자
        UserEntity user = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(user);

        // when
        Optional<UserEntity> result = userRepository.findByUsername(username);

        // then
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertNotNull(result.get().getCreatedAt());
    }

    // [실패] 사용자 이름 미존재
    @Test
    @DisplayName("사용자 이름 단건 조회 실패: 해당 사용자가 존재하지 않을 경우, Optional 반환")
    void find_by_username_failure_not_found() {
        // given
        String username = "yushi";

        // when
        Optional<UserEntity> result = userRepository.findByUsername(username);

        // then
        assertTrue(result.isEmpty());
    }


    /*
        중복 검사
     */
    // [성공]
    @Test
    @DisplayName("다른 사용자의 이름과 중복될 경우, true 반환")
    void exists_by_username_success() {
        // given
        String username = "yushi";

        // 존재하는 사용자
        UserEntity user = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUsername(username);

        // then
        assertTrue(exists);
    }

    // [실패] 이메일 중복
    @Test
    @DisplayName("존재하지 않는 이메일일 경우, false 반환")
    void exists_by_email_success() {
        // given

        // when
        boolean result = userRepository.existsByEmail("yushi@wish.com");

        // then
        assertFalse(result);
    }
}
