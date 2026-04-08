package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("User 상태와 프로필 fetch join 조회 성공")
    void findAllWithStatusAndProfile_success() {
        BinaryContent profile = new BinaryContent("profile.png", 10L, "image/png");
        entityManager.persist(profile);

        User user = new User("dustle", "1234", "dustle@test.com");
        user.updateProfile(profile);
        entityManager.persist(user);
        entityManager.persist(new UserStatus(user, Instant.now()));
        flushAndClear();

        List<User> users = userRepository.findAllWithStatusAndProfile();
        PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        assertThat(users).hasSize(1);
        assertThat(unitUtil.isLoaded(users.get(0), "status")).isTrue();
        assertThat(unitUtil.isLoaded(users.get(0), "profile")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 username 조회 실패")
    void findByUsername_fail_notFound() {
        User user = new User("dustle", "1234", "dustle@test.com");
        entityManager.persist(user);
        flushAndClear();

        assertThat(userRepository.findByUsername("missing")).isEmpty();
    }

    @Test
    @DisplayName("User 페이징 및 정렬 조회 성공")
    void findAll_success_withPagingAndSort() {
        entityManager.persist(new User("charlie", "1234", "charlie@test.com"));
        entityManager.persist(new User("alpha", "1234", "alpha@test.com"));
        entityManager.persist(new User("bravo", "1234", "bravo@test.com"));
        flushAndClear();

        Page<User> page = userRepository.findAll(
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "username"))
        );

        assertThat(page.getContent()).extracting(User::getUsername)
                .containsExactly("alpha", "bravo");
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("범위를 벗어난 User 페이지 조회 실패")
    void findAll_fail_whenPageOutOfRange() {
        entityManager.persist(new User("alpha", "1234", "alpha@test.com"));
        flushAndClear();

        Page<User> page = userRepository.findAll(
                PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "username"))
        );

        assertThat(page.getContent()).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
