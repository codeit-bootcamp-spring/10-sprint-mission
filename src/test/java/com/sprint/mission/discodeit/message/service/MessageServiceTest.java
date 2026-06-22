package com.sprint.mission.discodeit.message.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.message.application.mapper.MessageDomainMapper;
import com.sprint.mission.discodeit.message.application.mapper.MessagePayloadMapper;
import com.sprint.mission.discodeit.message.application.service.MessageService;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.exception.MessageErrorCode;
import com.sprint.mission.discodeit.message.domain.exception.MessageException;
import com.sprint.mission.discodeit.message.domain.provider.MessageBinaryContentResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageChannelResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageNotifier;
import com.sprint.mission.discodeit.message.domain.provider.MessageUserResolver;
import com.sprint.mission.discodeit.message.domain.repository.MessageRepository;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.support.mapper.DomainMapperContainer;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@DisplayName("MessageService Test")
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private MessageChannelResolver channelResolver;
  @Mock
  private MessageUserResolver userResolver;
  @Mock
  private MessageBinaryContentResolver binaryContentResolver;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private MessageNotifier notifier;
  @Spy
  private MessageDomainMapper domainMapper = DomainMapperContainer.get(MessageDomainMapper.class);
  @Spy
  private MessagePayloadMapper payloadMapper = DomainMapperContainer.get(
      MessagePayloadMapper.class);
  @InjectMocks
  private MessageService messageService;

  @Test
  @DisplayName("create - 메시지를 성공적으로 생성한다.")
  void create_success() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    List<UUID> attachmentIds = List.of(UUID.randomUUID());

    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId,
        attachmentIds);

    User author = mock(User.class);
    Channel channel = mock(Channel.class);
    BinaryContent content = mock(BinaryContent.class);

    given(userResolver.getProxyOrThrow(authorId)).willReturn(author);
    given(channelResolver.getProxyOrThrow(channelId)).willReturn(channel);
    given(binaryContentResolver.getProxyOrThrow(attachmentIds)).willReturn(List.of(content));

    Message result = messageService.create(request);

    assertThat(result).isNotNull();
    verify(messageRepository).save(any(Message.class));
    verify(notifier).notifyCreated(eq(channelId), any());
  }

  @Test
  @DisplayName("findSliceByChannelId - 채널 ID로 메시지를 조회한다.")
  void findSliceByChannelId_success() {
    UUID channelId = UUID.randomUUID();
    Pageable pageable = mock(Pageable.class);
    Slice<Message> messageSlice = new SliceImpl<>(List.of(mock(Message.class)));

    given(messageRepository.findSliceByChannel_Id(channelId, pageable)).willReturn(messageSlice);

    Slice<Message> result = messageService.findSliceByChannelId(channelId, pageable);

    assertThat(result).isEqualTo(messageSlice);
  }

  @Test
  @DisplayName("update - 메시지를 성공적으로 수정한다.")
  void update_success() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");
    Message message = mock(Message.class);
    Channel channel = mock(Channel.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(message.getChannel()).willReturn(channel);
    given(channel.getId()).willReturn(UUID.randomUUID());

    Message result = messageService.update(messageId, request);

    assertThat(result).isNotNull();
    verify(notifier).notifyUpdated(any(), any());
  }

  @Test
  @DisplayName("update - 존재하지 않는 메시지 수정 시 예외를 던진다")
  void update_fail_not_found() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = mock(MessageUpdateRequest.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageException.class)
        .hasFieldOrPropertyWithValue("errorCode", MessageErrorCode.MESSAGEID_NOT_FOUND);
  }

  @Test
  @DisplayName("delete - 메시지를 성공적으로 삭제한다.")
  void delete_success() {
    UUID messageId = UUID.randomUUID();
    Message message = mock(Message.class);
    Channel channel = mock(Channel.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(message.getChannel()).willReturn(channel);
    given(channel.getId()).willReturn(UUID.randomUUID());

    messageService.delete(messageId);

    verify(messageRepository).delete(message);
    verify(notifier).notifyDeleted(any(), any());
  }

  @Test
  @DisplayName("delete - 존재하지 않는 메시지 삭제 시 예외를 던진다")
  void delete_fail_not_found() {
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageException.class)
        .hasFieldOrPropertyWithValue("errorCode", MessageErrorCode.MESSAGEID_NOT_FOUND);
  }
}
