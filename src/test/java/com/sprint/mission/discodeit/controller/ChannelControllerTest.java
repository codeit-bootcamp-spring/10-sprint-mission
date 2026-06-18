package com.sprint.mission.discodeit.controller;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.NotAllowedInPrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(
    controllers = ChannelController.class,
    // 테스트할 때 검증 필터를 스캔 대상에서 제외
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
@ActiveProfiles("test")
@Import({GlobalExceptionHandler.class})
@Tag("unit")
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("성공: 공개 채널 생성 성공(201 created)")
  void createPublicChannelSuccess() throws Exception {
    //given
    PublicChannelCreateRequest dto = new PublicChannelCreateRequest("channelName",
        "channelDescription");
    UUID channelId = UUID.randomUUID();
    UserDto userDto1 = new UserDto(UUID.randomUUID(), "test1", "test1@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(UUID.randomUUID(), "test2", "test2@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    UserDto userDto3 = new UserDto(UUID.randomUUID(), "test3", "test3@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    List<UserDto> users = List.of(userDto1, userDto2, userDto3);
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, dto.name(),
        dto.description(), Instant.now(), Instant.now(), null, users);

    given(channelService.create(dto)).willReturn(channelDto);

    //DTO > JSON
    String body = objectMapper.writeValueAsString(dto);

    //when & then
    mockMvc.perform(post("/api/channels/public")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("channelName"))
        .andExpect(jsonPath("$.description").value("channelDescription"))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
  }

  @Test
  @DisplayName("성공: 비공개 채널 생성 성공(201 created)")
  void createPrivateChannelSuccess() throws Exception {
    //given
    UUID u1Id = UUID.randomUUID();
    UUID u2Id = UUID.randomUUID();
    UUID u3Id = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(List.of(u1Id, u2Id, u3Id));
    UserDto userDto1 = new UserDto(u1Id, "test1", "test1@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(u2Id, "test2", "test2@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto3 = new UserDto(u3Id, "test3", "test3@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    List<UserDto> participants = List.of(userDto1, userDto2, userDto3);
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PRIVATE, null,
        null, Instant.now(), Instant.now(), null, participants);

    given(channelService.create(dto)).willReturn(channelDto);

    //DTO > JSON
    String body = objectMapper.writeValueAsString(dto);

    //when & then
    mockMvc.perform(post("/api/channels/private")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
        .andExpect(jsonPath("$.participants.size()").value(participants.size()));
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디가 포함된 참여자 목록으로 비공개 채널 생성 실패(404 not found)")
  void createPrivateChannelWithNotExistedUserIdFailure() throws Exception {
    //given
    UUID u1Id = UUID.randomUUID();
    UUID u2Id = UUID.randomUUID();
    UUID wrongId = UUID.randomUUID();
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(List.of(u1Id, u2Id, wrongId));

    given(channelService.create(dto)).willThrow(new UserNotFoundException());

    //DTO > JSON
    String body = objectMapper.writeValueAsString(dto);

    //when & then
    mockMvc.perform(post("/api/channels/private")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 공개 채널 정보 수정 성공(200 ok)")
  void updatePublicChannelSuccess() throws Exception {
    //given
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newChannelName",
        "newChannelDescription");
    UUID channelId = UUID.randomUUID();
    UserDto userDto1 = new UserDto(UUID.randomUUID(), "test1", "test1@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(UUID.randomUUID(), "test2", "test2@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    UserDto userDto3 = new UserDto(UUID.randomUUID(), "test3", "test3@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    List<UserDto> users = List.of(userDto1, userDto2, userDto3);
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, dto.name(),
        dto.description(), Instant.now(), Instant.now(), Instant.now(), users);

    given(channelService.update(channelId, dto)).willReturn(channelDto);

    //DTO > JSON
    String body = objectMapper.writeValueAsString(dto);

    //when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("newChannelName"))
        .andExpect(jsonPath("$.description").value("newChannelDescription"))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
  }

  @Test
  @DisplayName("실패: 비공개 채널 정보 수정 실패(200 ok)")
  void updatePrivateChannelFailure() throws Exception {
    //given
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newChannelName",
        "newChannelDescription");
    UUID privateChannelId = UUID.randomUUID();

    given(channelService.update(privateChannelId, dto)).willThrow(
        new NotAllowedInPrivateChannelException());

    //DTO > JSON
    String body = objectMapper.writeValueAsString(dto);

    //when & then
    mockMvc.perform(patch("/api/channels/{channelId}", privateChannelId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.exceptionType").value(
            NotAllowedInPrivateChannelException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 채널 삭제 성공(204 No content)")
  void deleteChannelSuccess() throws Exception {
    //given
    UUID channelId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(channelService).should(times(1)).delete(channelId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 채널 아이디로 채널 삭제 실패(404 Not found)")
  void deleteChannelByWrongChannelIdFailure() throws Exception {
    //given
    UUID wrongChannelId = UUID.randomUUID();
    willThrow(new ChannelNotFoundException()).given(channelService).delete(eq(wrongChannelId));

    //when & then
    mockMvc.perform(delete("/api/channels/{channelId}", wrongChannelId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(ChannelNotFoundException.class.getSimpleName()));

  }

  @Test
  @DisplayName("성공: 사용자 아이디로 참여 채널 목록 조회 성공(200 Ok)")
  void findChannelsByUserSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UserDto userDto1 = new UserDto(userId, "test1", "test1@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(UUID.randomUUID(), "test2", "test2@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    UserDto userDto3 = new UserDto(UUID.randomUUID(), "test3", "test3@test.com", Role.USER, null,
        true,
        Instant.now(),
        Instant.now());
    ChannelDto channelDto1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "c1",
        "d1", Instant.now(), Instant.now(), Instant.now(), List.of(userDto1, userDto2, userDto3));
    ChannelDto channelDto2 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "c2",
        "d2", Instant.now(), Instant.now(), Instant.now(), List.of(userDto1, userDto2, userDto3));
    ChannelDto channelDto3 = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "c3",
        "d3", Instant.now(), Instant.now(), Instant.now(), List.of(userDto1, userDto2));

    given(channelService.findAllByUserId(eq(userId))).willReturn(
        List.of(channelDto1, channelDto2, channelDto3));

    //when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(userId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(3));

  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 참여 채널 목록 조회 실패(404 not found)")
  void findChannelsByWrongUserFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    given(channelService.findAllByUserId(eq(wrongUserId))).willThrow(new UserNotFoundException());

    //when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(wrongUserId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(
            UserNotFoundException.class.getSimpleName()));
  }
}