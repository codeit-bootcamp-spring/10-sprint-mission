package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest // 스프링 컨텍스트 로드
@AutoConfigureMockMvc
@ActiveProfiles("test") // H2 인메모리 및 테스트 설정
@Transactional // 테스트마다 트랜잭션 롤백
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Nested
  @DisplayName("POST /api/users - 유저 생성")
  class CreateUser {

    @Test
    @DisplayName("성공: 정상 데이터로 요청 시 DB에 저장된다")
    void success() throws Exception {
      UserCreateRequest requestDto = new UserCreateRequest("integUser", "integ@test.com",
          "Password123");
      MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest",
          "userCreateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      mockMvc.perform(
              multipart("/api/users").file(requestPart).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isCreated());

      List<User> users = userRepository.findAll();
      assertThat(users).hasSize(1);
      assertThat(users.get(0).getUsername()).isEqualTo("integUser");
    }

    @Test
    @DisplayName("실패: 이메일 형식이 틀리면 400 에러가 발생한다")
    void fail_invalidEmail() throws Exception {
      UserCreateRequest requestDto = new UserCreateRequest("integUser", "invalid-email",
          "Password123");
      MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest",
          "userCreateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      mockMvc.perform(
              multipart("/api/users").file(requestPart).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isBadRequest());
      assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("실패: 비밀번호가 조건(영문, 숫자 포함 8자 이상)을 만족하지 않으면 400 에러가 발생한다")
    void fail_invalidPassword() throws Exception {
      // 비밀번호 "123" (길이 부족, 영문 없음)
      UserCreateRequest requestDto = new UserCreateRequest("integUser", "integ@test.com", "123");
      MockMultipartFile requestPart = new MockMultipartFile(
          "userCreateRequest", "userCreateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
      );

      mockMvc.perform(
              multipart("/api/users").file(requestPart).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isBadRequest()); // 400 Bad Request 검증

      // DB에 데이터가 들어가지 않았는지 확인
      assertThat(userRepository.findAll()).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/users - 유저 목록 조회")
  class FindAllUsers {

    @Test
    @DisplayName("성공: DB에 저장된 유저 목록을 반환한다")
    void success() throws Exception {
      User savedUser = userRepository.save(
          new User("userA", "a@test.com", "Password123", null));
      userStatusRepository.save(new UserStatus(savedUser, java.time.Instant.now()));

      mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("성공: DB가 비어있으면 빈 배열을 반환한다")
    void success_empty() throws Exception {
      mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PATCH /api/users/{userId} - 유저 수정")
  class UpdateUser {

    @Test
    @DisplayName("성공: 정보 수정 시 DB 값이 변경된다")
    void success() throws Exception {
      User savedUser = userRepository.save(
          new User("oldName", "old@test.com", "Password123", null));
      UserUpdateRequest requestDto = new UserUpdateRequest("newName", "new@test.com", "NewPass123");
      MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest",
          "userUpdateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      mockMvc.perform(
              multipart("/api/users/{userId}", savedUser.getId()).file(requestPart).with(request -> {
                request.setMethod("PATCH");
                return request;
              }).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isOk());

      User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
      assertThat(updatedUser.getUsername()).isEqualTo("newName");
    }

    @Test
    @DisplayName("실패: 유효하지 않은 이메일로 수정 시도 시 400 에러가 발생한다")
    void fail_invalidEmail() throws Exception {
      User savedUser = userRepository.save(
          new User("oldName", "old@test.com", "Password123", null));
      UserUpdateRequest requestDto = new UserUpdateRequest("newName", "invalid", "NewPass123");
      MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest",
          "userUpdateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      mockMvc.perform(
              multipart("/api/users/{userId}", savedUser.getId()).file(requestPart).with(request -> {
                request.setMethod("PATCH");
                return request;
              }).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패: 유효하지 않은 비밀번호로 수정 시도 시 400 에러가 발생한다")
    void fail_invalidPassword() throws Exception {
      User savedUser = userRepository.save(
          new User("oldName", "old@test.com", "Password123", null));

      // 숫자 없는 비밀번호 생성
      UserUpdateRequest requestDto = new UserUpdateRequest("newName", "new@test.com",
          "passwordonly");
      MockMultipartFile requestPart = new MockMultipartFile(
          "userUpdateRequest", "userUpdateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
      );

      mockMvc.perform(multipart("/api/users/{userId}", savedUser.getId())
              .file(requestPart)
              .with(request -> {
                request.setMethod("PATCH");
                return request;
              })
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isBadRequest()); // 400 Bad Request 검증
    }
  }

  @Nested
  @DisplayName("DELETE /api/users/{userId} - 유저 삭제")
  class DeleteUser {

    @Test
    @DisplayName("성공: 유저 삭제 시 DB에서 지워진다")
    void success() throws Exception {
      User savedUser = userRepository.save(
          new User("deleteMe", "del@test.com", "Password123", null));
      mockMvc.perform(delete("/api/users/{userId}", savedUser.getId()))
          .andExpect(status().isNoContent());
      assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저 ID로 요청 시 에러가 발생한다")
    void fail_notFound() throws Exception {
      mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID()))
          .andExpect(status().is4xxClientError());
    }
  }
}
