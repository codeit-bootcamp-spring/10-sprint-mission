package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageAuthorNotInChannelException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageMapper messageMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(channelId, userId, "Hello");
    Channel channel = new Channel("testChannel", ChannelType.PUBLIC, "topic");
    User user = new User("testUser", "test@email.com", "password", null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(readStatusRepository.findByUser_IdAndChannel_Id(any(UUID.class),
        any(UUID.class))).willReturn(Optional.of(new ReadStatus(user, channel)));

    // when
    messageService.createMessage(request, Collections.emptyList());

    // then
    then(messageRepository).should().save(any(Message.class));
    then(messageMapper).should().toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 채널에 참여하지 않은 사용자")
  void createMessage_Fail_UserNotInChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(channelId, userId, "Hello");
    Channel channel = new Channel("testChannel", ChannelType.PRIVATE, "topic");
    User user = new User("testUser", "test@email.com", "password", null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(readStatusRepository.findByUser_IdAndChannel_Id(any(UUID.class),
        any(UUID.class))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.createMessage(request, Collections.emptyList()))
        .isInstanceOf(MessageAuthorNotInChannelException.class);

    then(messageRepository).should(times(0)).save(any(Message.class));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    Slice<Message> messages = new SliceImpl<>(Collections.emptyList());
    given(messageRepository.findByChannel_Id(any(UUID.class), any(Pageable.class))).willReturn(
        messages);

    // when
    messageService.findAllByChannelId(channelId, null, 20);

    // then
    then(messageRepository).should().findByChannel_Id(any(UUID.class), any(Pageable.class));
    then(messageMapper).should(times(0)).toDto(any(Message.class));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 실패 - 메시지 없음")
  void findAllByChannelId_Fail_NoMessagesFound() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Slice<Message> messages = new SliceImpl<>(Collections.emptyList());
    given(messageRepository.findByChannel_IdAndCreatedAtBefore(any(UUID.class), any(Instant.class),
        any(Pageable.class))).willReturn(messages);

    // when
    Slice<com.sprint.mission.discodeit.dto.MessageDto> result = messageService.findAllByChannelId(channelId, cursor, 20);

    // then
    assertThat(result).isEmpty();
    then(messageRepository).should()
        .findByChannel_IdAndCreatedAtBefore(any(UUID.class), any(Instant.class),
            any(Pageable.class));
    then(messageMapper).should(times(0)).toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("new content");
    Message message = new Message(null, null, "old content");
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    // when
    messageService.updateMessage(messageId, request);

    // then
    assertThat(message.getContent()).isEqualTo(request.getNewContent());
    then(messageMapper).should().toDto(message);
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void updateMessage_Fail_MessageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("new content");
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.updateMessage(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() {
    // given
    UUID messageId = UUID.randomUUID();
    Message message = new Message(null, null, "content");
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    // when
    messageService.deleteMessage(messageId);

    // then
    then(messageRepository).should().delete(message);
    then(binaryContentRepository).should(times(0)).delete(any());
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
  void deleteMessage_Fail_MessageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.deleteMessage(messageId))
        .isInstanceOf(MessageNotFoundException.class);
    then(messageRepository).should(times(0)).delete(any());
  }
}
