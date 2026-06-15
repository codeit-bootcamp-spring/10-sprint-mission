package com.sprint.mission.discodeit.message.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.api.response.PageResponse;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.exception.MessageErrorCode;
import com.sprint.mission.discodeit.message.domain.exception.MessageException;
import com.sprint.mission.discodeit.message.domain.provider.MessageBinaryContentResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageChannelResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageUserResolver;
import com.sprint.mission.discodeit.message.infra.repository.MessageRepository;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageMapper;
import com.sprint.mission.discodeit.support.mapper.MapperContainer;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Spy
  private MessageMapper messageMapper = MapperContainer.get(MessageMapper.class);
  @Mock
  private MessageChannelResolver channelProvider;
  @Mock
  private MessageUserResolver userProvider;
  @Mock
  private MessageBinaryContentResolver binaryContentProvider;

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
    MessageResponse response = mock(MessageResponse.class);
    User author = mock(User.class);
    Channel channel = mock(Channel.class);
    BinaryContent content = mock(BinaryContent.class);

    given(userProvider.getProxyOrThrow(authorId)).willReturn(author);
    given(channelProvider.getProxyOrThrow(channelId)).willReturn(channel);
    given(binaryContentProvider.getProxyOrThrow(attachmentIds)).willReturn(List.of(content));
    given(messageMapper.toDto(any(Message.class))).willReturn(response);

    MessageResponse result = messageService.create(request);

    assertThat(result).isEqualTo(response);
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("findSliceByChannelId - 채널 ID로 메시지를 조회한다.")
  void findSliceByChannelId_success() {
    UUID channelId = UUID.randomUUID();
    Pageable pageable = mock(Pageable.class);
    Slice<Message> messageSlice = new SliceImpl<>(List.of(mock(Message.class)));
    PageResponse<MessageResponse> pageResponse = mock(PageResponse.class);

    given(messageRepository.findSliceByChannel_Id(channelId, pageable)).willReturn(messageSlice);
    given(messageMapper.fromSlice(messageSlice)).willReturn(pageResponse);

    PageResponse<MessageResponse> result = messageService.findSliceByChannelId(channelId, pageable);

    assertThat(result).isEqualTo(pageResponse);
  }

  @Test
  @DisplayName("update - 메시지를 성공적으로 수정한다.")
  void update_success() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = mock(MessageUpdateRequest.class);
    Message message = mock(Message.class);
    MessageResponse response = mock(MessageResponse.class);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(response);

    MessageResponse result = messageService.update(messageId, request);

    assertThat(result).isEqualTo(response);
    verify(messageMapper).partialUpdate(request, message);
  }

  @Test
  @DisplayName("update - 존재하지 않는 메시지 수정 시 예외를 던진다.")
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

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    verify(messageRepository).delete(message);
  }

  @Test
  @DisplayName("delete - 존재하지 않는 메시지 삭제 시 예외를 던진다.")
  void delete_fail_not_found() {
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageException.class)
        .hasFieldOrPropertyWithValue("errorCode", MessageErrorCode.MESSAGEID_NOT_FOUND);
  }
}
