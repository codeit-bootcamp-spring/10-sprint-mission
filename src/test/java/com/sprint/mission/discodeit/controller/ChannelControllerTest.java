package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotBeUpdatedException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청 시뮬레이터

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ChannelService channelService;

    // `@EnableJpaAuditing`이 애플리케이션 시작 클래스에 설정되어 있어서 JPA Auditing가 활성화됨
    // `jpaAuditingHandler`는 `jpaMappingContext`를 필요로 함
    // 근데 API 슬라이스 테스트에는 Entity 메타 모델이 없어서 오류가 남
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    Instant now;
    Instant nowMinus5;
    Instant nowMinus10;
    Instant nowMinus15;
    Instant nowMinus20;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        nowMinus5 = now.minus(5, ChronoUnit.MINUTES);
        nowMinus10 = now.minus(10, ChronoUnit.MINUTES);
        nowMinus15 = now.minus(15, ChronoUnit.MINUTES);
        nowMinus20 = now.minus(20, ChronoUnit.MINUTES);
    }

    private UserDto createUserDto(String email, String username, BinaryContentDto profile, boolean isOnline) {
        UUID authorId = UUID.randomUUID();
        return new UserDto(authorId, username, email, profile, isOnline, Role.USER);
    }

    private ChannelDto createChannelDto(ChannelType type, String name, String description, List<UserDto> participants, Instant lastMessageAt) {
        UUID channelId = UUID.randomUUID();
        return new ChannelDto(channelId, type, name, description, participants, lastMessageAt);
    }

    @Nested
    @DisplayName("공개/비공개 채널 생성 API 테스트")
    class createChannel {

        @Test
        @DisplayName("공개 채널을 생성하면 201 상태 코드와 공개 채널 정보를 반환한다.")
        void success_create_public_channel() throws Exception {
            // given(준비)
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("testPublicChannel", "test public channel입니다.");

            ChannelDto expectedChannelDto = createChannelDto(ChannelType.PUBLIC, request.name(), request.description(), null, null);

            given(channelService.createPublicChannel(request)).willReturn(expectedChannelDto);

            // when(실행), then(검증)
            mockMvc.perform(post("/api/channels/public")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(expectedChannelDto.id().toString()))
                    .andExpect(jsonPath("$.type").value(expectedChannelDto.type().toString()))
                    .andExpect(jsonPath("$.name").value(expectedChannelDto.name()))
                    .andExpect(jsonPath("$.description").value(expectedChannelDto.description()));
        }

        @Test
        @DisplayName("비공개 채널을 생성하면 201 상태 코드와 비공개 채널 정보를 반환한다.")
        void success_create_private_channel() throws Exception {
            // given(준비)
            UserDto userDto1 = createUserDto("test1@gmail.com", "test1", null, true);
            UserDto userDto2 = createUserDto("test2@gmail.com", "test2", null, true);
            List<UserDto> participants = List.of(userDto1, userDto2);

            UUID userId1 = userDto1.id();
            UUID userId2 = userDto2.id();
            List<UUID> participantIds = List.of(userId1, userId2);

            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            ChannelDto expectedChannelDto = createChannelDto(ChannelType.PRIVATE, null, null, participants, null);

            given(channelService.createPrivateChannel(request)).willReturn(expectedChannelDto);

            // when(실행), then(검증)
            mockMvc.perform(post("/api/channels/private")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.participants", hasSize(2)))
                    .andExpect(jsonPath("$.participants[0].id").value(expectedChannelDto.participants().get(0).id().toString()))
                    .andExpect(jsonPath("$.participants[1].id").value(expectedChannelDto.participants().get(1).id().toString()));
        }
    }

    @Nested
    @DisplayName("채널 목록 조회 API 테스트")
    class findAllChannelList {

        @Test
        @DisplayName("채널 메시지 목록을 조회하면 200 상태 코드와 채널 목록이 반환된다.")
        void success_find_all_channel_list() throws Exception {
            // given(준비)
            UserDto userDto1 = createUserDto("test1@gmail.com", "test1", null, true);
            UserDto userDto2 = createUserDto("test2@gmail.com", "test2", null, true);
            List<UserDto> participants = List.of(userDto1, userDto2);

            ChannelDto channelDto1 = createChannelDto(ChannelType.PUBLIC, "test1Channel", "test1 Channel입니다.", null, now);
            ChannelDto channelDto2 = createChannelDto(ChannelType.PRIVATE, null, null, participants, nowMinus15);
            ChannelDto channelDto3 = createChannelDto(ChannelType.PRIVATE, null, null, participants, nowMinus5);
            List<ChannelDto> channelDtoList = List.of(channelDto1, channelDto2, channelDto3);

            UUID requestUserId = userDto1.id();

            given(channelService.findAllByUserId(requestUserId)).willReturn(channelDtoList);

            // when(실행), then(검증)
            mockMvc.perform(get("/api/channels")
                            .param("userId", requestUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(channelDtoList.size())))
                    .andExpect(jsonPath("$[0].id").value(channelDto1.id().toString()))
                    .andExpect(jsonPath("$[1].id").value(channelDto2.id().toString()))
                    .andExpect(jsonPath("$[2].id").value(channelDto3.id().toString()))
                    .andExpect(jsonPath("$[1].participants[0].id").value(channelDto2.participants().get(0).id().toString()))
                    .andExpect(jsonPath("$[2].participants[0].id").value(channelDto3.participants().get(0).id().toString()));
        }

        @Test
        @DisplayName("채널 메시지 목록을 조회 시 채널이 하나도 없을 경우 200 상태 코드와 빈 채널 목록이 반환된다.")
        void success_find_empty_channel_list() throws Exception {
            // given(준비)
            UUID requestUserId = UUID.randomUUID();

            given(channelService.findAllByUserId(requestUserId)).willReturn(List.of());

            // when(실행), then(검증)
            mockMvc.perform(get("/api/channels")
                            .param("userId", requestUserId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("채널 정보 수정 API 테스트")
    class updateChannel {

        @Test
        @DisplayName("특정 채널 정보를 수정하면 200 상태 코드와 수정된 채널 정보를 반환한다.")
        void success_update_channel_by_id() throws Exception {
            // given(준비)
            UUID requestChannelId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateTestName", "updateTestDescription");

            ChannelDto channelDto = createChannelDto(ChannelType.PUBLIC, "testChannel", "test Channel입니다.", null, now);

            ChannelDto expectedChannelDto = new ChannelDto(channelDto.id(), channelDto.type(), request.newName(), request.newDescription(), null, channelDto.lastMessageAt());

            given(channelService.update(requestChannelId, request)).willReturn(expectedChannelDto);

            // when(실행), then(검증)
            mockMvc.perform(patch("/api/channels/{channelId}", requestChannelId)
                            .content(om.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(expectedChannelDto.id().toString()))
                    .andExpect(jsonPath("$.name").value(expectedChannelDto.name()))
                    .andExpect(jsonPath("$.description").value(expectedChannelDto.description()));
        }

        @Test
        @DisplayName("비공개 채널을 수정하려고 하면 400 상태 코드와 PrivateChannelCannotBeUpdatedException 에러 응답을 반환한다.")
        void fail_update_channel_by_id_when_private_channel_update() throws Exception {
            // given(준비)
            UUID requestChannelId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateTestName", "updateTestDescription");

            given(channelService.update(requestChannelId, request)).willThrow(new PrivateChannelCannotBeUpdatedException(requestChannelId));

            // when(실행), then(검증)
            mockMvc.perform(patch("/api/channels/{channelId}", requestChannelId)
                            .content(om.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.PRIVATE_CHANNEL_CANNOT_BE_UPDATED.toString()))
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.exceptionType").value(PrivateChannelCannotBeUpdatedException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.channelId").value(requestChannelId.toString()));
        }

        @Test
        @DisplayName("채널을 찾을 수 없으면 404 상태 코드와 ChannelNotFoundException 예외 응답을 반환한다.")
        void fail_update_channel_by_id_when_channel_not_found() throws Exception {
            // given(준비)
            UUID requestChannelId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateTestName", "updateTestDescription");

            given(channelService.update(requestChannelId, request)).willThrow(new ChannelNotFoundException(requestChannelId));

            // when(실행), then(검증)
            mockMvc.perform(patch("/api/channels/{channelId}", requestChannelId)
                            .content(om.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.CHANNEL_NOT_FOUND.toString()))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.exceptionType").value(ChannelNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.channelId").value(requestChannelId.toString()));
        }
    }

    @Nested
    @DisplayName("채널 삭제 API 테스트")
    class deleteChannel {

        @Test
        @DisplayName("특정 채널을 삭제하면 204 상태 코드를 반환한다.")
        void success_delete_channel_by_id () throws Exception {
            // given(준비)
            UUID requestChannelId = UUID.randomUUID();

            willDoNothing().given(channelService).delete(requestChannelId);

            // when(실행), then(검증)
            mockMvc.perform(delete("/api/channels/{channelId}", requestChannelId))
                    .andExpect(status().isNoContent());

            verify(channelService).delete(requestChannelId);
        }

        @Test
        @DisplayName("채널을 찾을 수 없으면 404 상태 코드와 ChannelNotFoundException 예외 응답을 반환한다.")
        void fail_delete_channel_by_id_when_channel_not_found () throws Exception {
            // given(준비)
            UUID requestChannelId = UUID.randomUUID();

            willThrow(new ChannelNotFoundException(requestChannelId)).given(channelService).delete(requestChannelId);

            // when(실행), then(검증)
            mockMvc.perform(delete("/api/channels/{channelId}", requestChannelId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.CHANNEL_NOT_FOUND.toString()))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.exceptionType").value(ChannelNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.channelId").value(requestChannelId.toString()));
        }
    }
}