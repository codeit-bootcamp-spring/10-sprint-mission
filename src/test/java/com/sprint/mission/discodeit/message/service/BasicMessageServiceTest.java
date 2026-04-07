package com.sprint.mission.discodeit.message.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.common.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.common.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.paging.dto.PageResponse;
import com.sprint.mission.discodeit.paging.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.user.dto.UserDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  JPAChannelRepository jpaChannelRepository;

  @Mock
  JPAUserRepository jpaUserRepository;

  @Mock
  JPAMessageRepository jpaMessageRepository;

  @Mock
  MessageMapper messageMapper;

  @Mock
  PageResponseMapper pageResponseMapper;

  @InjectMocks
  BasicMessageService basicMessageService;

  private Channel mockChannel;
  private User mockUser;
  private MessageDto mockMessageDto;
  private Message mockMessage;

  @BeforeEach
  void setUp() {
    mockChannel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널 내용");
    mockUser = new User("테스트 유저", "test@test.com", "password", null);
    UserDto mockUserDto = new UserDto(UUID.randomUUID(), Instant.now(), Instant.now(), "이름", "이메일",
        null,
        false);
    mockMessageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
        "테스트 내용", UUID.randomUUID(), mockUserDto, null);
    mockMessage = new Message("테스트 내용", mockChannel, mockUser, List.of());

  }

  @Nested
  @DisplayName("메시지 생성 테스트")
  class MessageCreate {

    private MessageCreateRequest messageCreateRequest;

    @BeforeEach
    void setUp() {
      messageCreateRequest = new MessageCreateRequest("테스트내용", UUID.randomUUID(),
          UUID.randomUUID());

    }

    @Test
    @DisplayName("메시지 생성 테스트 성공")
    void MessageCreateSuccess() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.of(mockChannel));
      given(jpaUserRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));
      given(jpaMessageRepository.save(any(Message.class))).willAnswer(
          invocation -> invocation.getArgument(0));
      given(messageMapper.toDto(any(Message.class))).willReturn(mockMessageDto);
      //when
      MessageDto result = basicMessageService.create(messageCreateRequest, List.of());
      //then
      assertThat(result).isEqualTo(mockMessageDto);
      then(jpaChannelRepository).should().findById(any(UUID.class));
      then(jpaUserRepository).should().findById(any(UUID.class));
      then(jpaMessageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 테스트 실패")
    void MessageCreateFail() {
      //given
      given(jpaChannelRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(() -> basicMessageService.create(messageCreateRequest, List.of()))
          .isInstanceOf(ChannelNotFoundException.class);

    }
  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class MessageUpdate {

    private MessageUpdateRequest messageUpdateRequest;

    @BeforeEach
    void setUP() {
      messageUpdateRequest = new MessageUpdateRequest("테스트 수정 내용");
    }

    @Test
    @DisplayName("메시지 수정 테스트 성공")
    void MessageUpdateSuccess() {
      //given
      given(jpaMessageRepository.findById(any(UUID.class))).willReturn(Optional.of(mockMessage));
      given(messageMapper.toDto(any(Message.class))).willReturn(mockMessageDto);
      //when
      MessageDto result = basicMessageService.update(UUID.randomUUID(), messageUpdateRequest);
      //then
      assertThat(result).isEqualTo(mockMessageDto);
      then(jpaMessageRepository).should().findById(any(UUID.class));
    }

    @Test
    @DisplayName("메시지 수정 테스트 실패")
    void MessageUpdateFail() {
      //given
      given(jpaMessageRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicMessageService.update(UUID.randomUUID(), messageUpdateRequest)
      ).isInstanceOf(MessageNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class MessageDelete {

    @Test
    @DisplayName("메시지 삭제 테스트 성공")
    void MessageDeleteSuccess() {
      //given
      given(jpaMessageRepository.findById(any(UUID.class))).willReturn(Optional.of(mockMessage));
      //when
      basicMessageService.delete(UUID.randomUUID());
      //then
      then(jpaMessageRepository).should().findById(any(UUID.class));
      then(jpaMessageRepository).should().delete(mockMessage);
    }

    @Test
    @DisplayName("메시지 삭제 테스트 실패")
    void MessageDeleteFail() {
      //given
      given(jpaMessageRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicMessageService.delete(UUID.randomUUID())
      ).isInstanceOf(MessageNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("채널에 있는 메시지 조회 테스트")
  class MessageFindByChannelId {

    private Instant cursor;
    private Pageable pageable;
    private PageResponse<MessageDto> mockPageResponse;

    @BeforeEach
    void setUp() {
      cursor = Instant.now();
      pageable = PageRequest.of(0, 10);
      mockPageResponse = new PageResponse<>(
          List.of(mockMessageDto),
          null,
          10,
          false,
          null
      );
    }

    @Test
    @DisplayName("조회 테스트 성공")
    void MessageFindSuccess() {
      //given
      Slice<Message> mockSlice = new SliceImpl<>(List.of(mockMessage));
      given(jpaMessageRepository.findByChannelIdWithCursor(any(), any(), any())).willReturn(
          mockSlice);
      given(messageMapper.toDto(any(Message.class))).willReturn(mockMessageDto);
      given(pageResponseMapper.<MessageDto>slice(any(), any())).willReturn(mockPageResponse);

      //when
      PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(
          UUID.randomUUID(), cursor, pageable);

      //then
      assertThat(result).isNotNull();
      then(jpaMessageRepository).should().findByChannelIdWithCursor(any(), any(), any());
    }

    @Test
    @DisplayName("조회 테스트 실패")
    void MessageFindFail() {
      //given
      Slice<Message> emptySlice = new SliceImpl<>(List.of());
      given(jpaMessageRepository.findByChannelIdWithCursor(any(), any(), any()))
          .willReturn(emptySlice);
      given(pageResponseMapper.<MessageDto>slice(any(), any())).willReturn(
          new PageResponse<>(List.of(), null, 10, false, null));

      //when
      PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(
          UUID.randomUUID(), cursor, pageable);

      //then
      assertThat(result.content()).isEmpty();
    }
  }
}