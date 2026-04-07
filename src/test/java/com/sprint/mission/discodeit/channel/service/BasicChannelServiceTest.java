package com.sprint.mission.discodeit.channel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.channel.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.common.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.message.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  JPAChannelRepository jpaChannelRepository;
  @Mock
  JPAReadStatusRepository jpaReadStatusRepository;
  @Mock
  JPAMessageRepository jpaMessageRepository;
  @Mock
  JPAUserRepository jpaUserRepository;
  @Mock
  ChannelMapper channelMapper;

  @InjectMocks
  BasicChannelService basicChannelService;

  private ChannelDto mockDto;
  private Channel mockChannel;

  @BeforeEach
  void setUp() {
    mockDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "Mock", "테스트 DTO",
        List.of(), Instant.now());
    mockChannel = new Channel(ChannelType.PUBLIC, "Mock 채널", "테스트 채널");
  }

  @Nested
  @DisplayName("채널 생성 테스트")
  class ChannelCreate {

    @Nested
    @DisplayName("PUBLIC 채널 테스트")
    class PublicChannelCreate {

      private ChannelCreatePublicRequest channelCreatePublicRequest;

      @BeforeEach
      void setUp() {
        channelCreatePublicRequest = new ChannelCreatePublicRequest("테스트채널", "테스트채널설명");
      }

      @Test
      @DisplayName("Public 채널 생성 테스트 성공")
      void CreateChannelSuccess() {
        //given
        given(jpaChannelRepository.save(any(Channel.class))).willAnswer(
            invocation -> invocation.getArgument(0));
        given(channelMapper.toDto(any(Channel.class))).willReturn(mockDto);
        //when
        ChannelDto result = basicChannelService.create(channelCreatePublicRequest);
        //then
        assertThat(result).isEqualTo(mockDto);
        then(jpaChannelRepository).should().save(any(Channel.class));
      }

      @Test
      @DisplayName("Public 채널 생성 테스트 실패")
      void CreateChannelFail() {
        //given
        channelCreatePublicRequest = new ChannelCreatePublicRequest("", "테스트채널설명");
        //when,then
        assertThatThrownBy(() -> basicChannelService.create(channelCreatePublicRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("PRIVATE 채널 테스트")
    class PrivateChannlCreate {

      private ChannelCreatePrivateRequest channelCreatePrivateRequest;

      @BeforeEach
      void setUp() {
        channelCreatePrivateRequest = new ChannelCreatePrivateRequest(List.of());
      }

      @Test
      @DisplayName("PRIVATE 채널 생성 테스트 성공")
      void CreateChannelSuccess() {
        //given
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        ChannelCreatePrivateRequest request = new ChannelCreatePrivateRequest(participantIds);
        List<User> participants = List.of(
            new User("user1", "user1@test.com", "password", null),
            new User("user2", "user2@test.com", "password", null)
        );

        given(jpaChannelRepository.save(any(Channel.class))).willAnswer(
            invocation -> invocation.getArgument(0));
        given(jpaUserRepository.findAllById(request.participantIds())).willReturn(participants);
        given(jpaReadStatusRepository.saveAll(any())).willAnswer(
            invocation -> invocation.getArgument(0));
        given(channelMapper.toDto(any(Channel.class))).willReturn(mockDto);

        //when
        ChannelDto result = basicChannelService.create(request);

        //then
        assertThat(result).isEqualTo(mockDto);
        then(jpaChannelRepository).should().save(any(Channel.class));
        then(jpaReadStatusRepository).should().saveAll(any());
      }

      @Test
      @DisplayName("PRIVATE 채널 생성 테스트 실패")
      void CreateChannelFail() {
        //given
        given(jpaChannelRepository.save(any(Channel.class))).willAnswer(
            invocation -> invocation.getArgument(0));
        given(
            jpaUserRepository.findAllById(channelCreatePrivateRequest.participantIds())).willReturn(
            List.of());
        //when,then
        assertThatThrownBy(() -> basicChannelService.create(channelCreatePrivateRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }

    }

  }

  @Nested
  @DisplayName("채널 수정 테스트")
  class ChannelUpdate {

    private ChannelUpdateRequest channelUpdateRequest;

    @BeforeEach
    void setUp() {
      channelUpdateRequest = new ChannelUpdateRequest("newTestName", "newTestDesc");
    }

    @Test
    @DisplayName("채널 수정 테스트 성공")
    void ChannelUpdateSuccess() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.of(mockChannel));
      given(channelMapper.toDto(any(Channel.class))).willReturn(mockDto);
      //when
      ChannelDto result = basicChannelService.update(UUID.randomUUID(), channelUpdateRequest);
      //then
      assertThat(result).isEqualTo(mockDto);
      then(jpaChannelRepository).should().findById(any(UUID.class));
    }

    @Test
    @DisplayName("채널 수정 테스트 실패")
    void ChannelUpdateFail() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicChannelService.update(UUID.randomUUID(), channelUpdateRequest)
      ).isInstanceOf(ChannelNotFoundException.class);
    }

  }

  @Nested
  @DisplayName("채널 삭제 테스트")
  class ChannelDelete {

    @Test
    @DisplayName("채널 삭제 테스트 성공")
    void ChannelDeleteSuccess() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.of(mockChannel));
      //when
      basicChannelService.delete(UUID.randomUUID());
      //then
      then(jpaChannelRepository).should().findById(any(UUID.class));
      then(jpaChannelRepository).should().delete(mockChannel);

    }

    @Test
    @DisplayName("채널 삭제 테스트 실패")
    void ChannelDeleteFail() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicChannelService.delete(UUID.randomUUID())
      ).isInstanceOf(ChannelNotFoundException.class);
    }

  }

  @Nested
  @DisplayName("유저id로 채널 찾기 테스트")
  class ChannelFindByUserId {

    private ReadStatus mockReadStatus;
    private UUID userId;

    @BeforeEach
    void setUp() {
      User mockUser = new User("testUser", "test@test.com", "password", null);
      mockReadStatus = new ReadStatus(mockUser, mockChannel, Instant.now());
      userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("테스트 성공")
    void FindByUserIdSuccess() {
      //given
      given(jpaReadStatusRepository.findAllByUserId(any(UUID.class))).willReturn(
          List.of(mockReadStatus));
      given(jpaChannelRepository.findByTypeOrIdIn(any(), any())).willReturn(
          List.of(mockChannel));
      given(jpaMessageRepository.findLastMessageTimesByChannelIds(any())).willReturn(List.of());
      given(jpaReadStatusRepository.findAllByChannelIdIn(any())).willReturn(List.of());
      //when
      List<ChannelDto> result = basicChannelService.findByUserId(userId);
      //then
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("테스트 실패")
    void FindByUserIdFail() {
      //given
      given(jpaReadStatusRepository.findAllByUserId(any(UUID.class))).willReturn(
          List.of());
      given(jpaChannelRepository.findByTypeOrIdIn(any(), any())).willReturn(
          List.of());
      //when
      List<ChannelDto> result = basicChannelService.findByUserId(userId);
      //then
      assertThat(result).isEmpty();
    }

  }


}