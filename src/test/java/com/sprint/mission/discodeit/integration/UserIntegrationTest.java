package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStatusRepository userStatusRepository;

    @Autowired
    BinaryContentRepository binaryContentRepository;

    @Autowired
    BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("유저 생성부터 저장까지 정상 동작")
    @Transactional
    void user_creation_fully_works() {
        // given
        UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abc@a.com", "abc123");

        // when
        UserDto saved = userService.create(req, null);

        // then
        User user = userRepository.findByUsername(req.username()).orElseThrow(
            NoSuchElementException::new);
        UUID userId = user.getId();

        assertThat(saved.id()).isEqualTo(userId);
        assertThat(saved.username()).isEqualTo(user.getUsername());
        assertThat(saved.email()).isEqualTo(user.getEmail());
        assertThat(userStatusRepository.findByUserId(userId)).isPresent();
    }

    @Test
    @DisplayName("유저 삭제 요청부터 DB 반영까지 정상 동작")
    @Transactional
    void user_delete_fully_works() {
        // given
        UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abc@a.com", "abc123");
        UserDto userDto = userService.create(req, null);
        UUID userId = userDto.id();

        // when
        userService.delete(userId);

        // then
        assertThat(userStatusRepository.findByUserId(userId)).isEmpty();
        assertThat(userRepository.existsByEmail(userDto.email())).isFalse();
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userRepository.existsByUsername(userDto.username())).isFalse();
    }

    @Test
    @DisplayName("유저 수정 요청부터 DB 반영까지 정상 동작")
    @Transactional
    void user_update_fully_works() {
        // given
        UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abc@a.com", "abc123");
        UserDto userDto = userService.create(req, null);
        UUID userId = userDto.id();

        UserUpdateDTO updateReq = new UserUpdateDTO("def", "def@a.com", "def");

        byte[] bytes = "dummy_bytes".getBytes(StandardCharsets.UTF_8);

        BinaryContentDto binaryContentDto = new BinaryContentDto(UUID.randomUUID()
            , "dummyfile.jpg", bytes.length, "Image/png", bytes);

        // when
        UserDto updated = userService.update(userId, updateReq, binaryContentDto);

        // then
        assertThat(updated.profile()).isNotNull();
        assertThat(
            binaryContentRepository.existsById(updated.profile().id())).isTrue();
        assertThat(userRepository.existsByUsername(updateReq.newUsername())).isTrue();
        assertThat(userRepository.existsByEmail(updateReq.newEmail())).isTrue();
        assertThat(userRepository.findById(userId)).isNotEmpty();
    }

    @Test
    @DisplayName("유저 목록 조회 성공")
    @Transactional
    void user_get_list_fully_works() {
        // given
        UserCreateRequestDTO req1 = new UserCreateRequestDTO("abc", "a@a.com", "123");
        UserCreateRequestDTO req2 = new UserCreateRequestDTO("def", "b@a.com", "123");
        UserCreateRequestDTO req3 = new UserCreateRequestDTO("ghi", "c@a.com", "123");

        userService.create(req1, null);
        userService.create(req2, null);
        userService.create(req3, null);

        // when
        List<UserDto> users = userService.findAll();

        // then
        assertThat(users).isNotEmpty();
        assertThat(users.stream().map(UserDto::username).toList()).contains(req1.username());
        assertThat(users.stream().map(UserDto::username).toList()).contains(req2.username());
        assertThat(users.stream().map(UserDto::username).toList()).contains(req3.username());

    }

}
