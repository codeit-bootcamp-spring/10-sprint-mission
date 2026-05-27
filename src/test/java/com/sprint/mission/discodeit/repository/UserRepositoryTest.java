package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    Instant now;
    Instant nowMinus5;
    Instant nowMinus10;

    @BeforeEach
    void setUp() {
        binaryContentRepository.deleteAll();
        userRepository.deleteAll();

        now = Instant.now();
        nowMinus5 = now.minus(5, ChronoUnit.MINUTES);
        nowMinus10 = now.minus(10, ChronoUnit.MINUTES);
    }

    private User createUser(String email, String username, String password, BinaryContent profile) {
        User author = new User(email, username, password, profile);

        return userRepository.save(author);
    }

    private BinaryContent createBinaryContent(String fileName, String contentType, Long size) {
        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        return binaryContentRepository.save(binaryContent);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 온라인 상태와 프로필을 포함한 사용자를 조회할 수 있다.")
    void find_user_by_userId_with_profile() {
        // given(준비)
        BinaryContent profile = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);
        User user = createUser("test1@gmail.com", "test1", "1234", profile);
        UUID userId = user.getId();

        // 영속성 해제
        testEntityManager.flush(); // 영속성 컨텍스트에만 반영되어 있던 변경 내용을 즉시 DB에 반영
        testEntityManager.clear(); // 현재 영속성 컨텍스트를 비움

        // when(실행)
        User result = userRepository.findByIdWithProfile(userId).orElseThrow();

        // then(검증)
        assertEquals(userId, result.getId());
        assertThat(Hibernate.isInitialized(result.getProfile())).isTrue();
    }

    @Test
    @DisplayName("사용자 이름으로 사용자 온라인 상태와 프로필을 포함한 사용자를 조회할 수 있다.")
    void find_user_by_username_with_profile() {
        // given(준비)
        BinaryContent profile = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);
        User user = createUser("test1@gmail.com", "test1", "1234", profile);
        UUID userId = user.getId();
        String username = user.getUsername();

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        User result = userRepository.findByUsernameWithProfile(username).orElseThrow();

        // then(검증)
        assertEquals(userId, result.getId());
        assertEquals(username, result.getUsername());
        assertThat(Hibernate.isInitialized(result.getProfile())).isTrue();
    }

    @Test
    @DisplayName("사용자별 온라인 상태와 프로필을 포함한 사용자 목록을 조회할 수 있다.")
    void find_All_user_list_with_profile() {
        // given(준비)
        BinaryContent profile1 = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);
        BinaryContent profile2 = createBinaryContent("test2Binary", "image/png", (long) "test2".getBytes().length);

        User user1 = createUser("test1@gmail.com", "test1", "1234", profile1);
        User user2 = createUser("test2@gmail.com", "test2", "1234", profile2);
        User user3 = createUser("test3@gmail.com", "test3", "1234", null);

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        List<User> result = userRepository.findAllWithProfile();

        // then(검증)
        assertEquals(3,  result.size());
        assertThat(result)
                .extracting(user -> user.getId())
                .containsExactlyInAnyOrder(user1.getId(), user2.getId(), user3.getId());

        assertThat(Hibernate.isInitialized(result.get(0).getProfile())).isTrue();
        assertThat(Hibernate.isInitialized(result.get(1).getProfile())).isTrue();
        assertThat(Hibernate.isInitialized(result.get(2).getProfile())).isTrue();
    }

    @Test
    @DisplayName("사용자들이 없을 경우, 빈 사용자 목록을 조회할 수 있다.")
    void find_empty_user_list_with_profile() {
        // when(실행)
        List<User> result = userRepository.findAllWithProfile();

        // then(검증)
        assertEquals(0,  result.size());
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("자신을 제외한 다른 사용자가 사용 중인 이메일과 동일하면 true를 반환할 수 있다.")
    void return_ture_when_another_user_used_email() {
        // given(준비)
        BinaryContent profile1 = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);

        User my = createUser("test1@gmail.com", "test1", "1234", profile1);
        User anotherUser = createUser("test2@gmail.com", "test2", "1234", null);

        String inputEmail = anotherUser.getEmail();

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        boolean result = userRepository.isEmailUsedByOther(my.getId(), inputEmail);

        // then(검증)
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자신을 제외한 다른 사용자가 사용 중인 이메일과 다르면 false를 반환할 수 있다.")
    void return_false_when_another_user_used_email() {
        // given(준비)
        BinaryContent profile1 = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);

        User my = createUser("test1@gmail.com", "test1", "1234", profile1);
        User anotherUser = createUser("test2@gmail.com", "test2", "1234", null);

        String inputEmail = "test3@gmail.com";

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        boolean result = userRepository.isEmailUsedByOther(my.getId(), inputEmail);

        // then(검증)
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("자신을 제외한 다른 사용자가 사용 중인 사용자 이름과 동일하면 true를 반환할 수 있다.")
    void return_true_when_another_user_used_username() {
        // given(준비)
        BinaryContent profile1 = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);

        User my = createUser("test1@gmail.com", "test1", "1234", profile1);
        User anotherUser = createUser("test2@gmail.com", "test2", "1234", null);

        String inputUsername = anotherUser.getUsername();

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        boolean result = userRepository.isUsernameUsedByOther(my.getId(), inputUsername);

        // then(검증)
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자신을 제외한 다른 사용자가 사용 중인 사용자 이름과 다르면 false를 반환할 수 있다.")
    void return_false_when_another_user_used_username() {
        // given(준비)
        BinaryContent profile1 = createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length);

        User my = createUser("test1@gmail.com", "test1", "1234", profile1);
        User anotherUser = createUser("test2@gmail.com", "test2", "1234", null);

        String inputUsername = "Jung";

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        boolean result = userRepository.isUsernameUsedByOther(my.getId(), inputUsername);

        // then(검증)
        assertThat(result).isFalse();
    }
}