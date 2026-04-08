package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  MessageRepository messageRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ChannelRepository channelRepository;
  @Mock
  BinaryContentRepository binaryContentRepository;
  @Mock
  BinaryContentStorage binaryContentStorage;
  @Mock
  PageResponseMapper pageMapper;
  @Mock
  MessageMapper messageMapper;

  @InjectMocks
  BasicMessageService messageService;

  @Nested
  class createMessage {

    @Test
    @DisplayName("userId, channelId와 메세지로 messageDto 반환")
    void should_return_messageDto_when_fields_are_provided() throws IOException {
      // given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      User user = new User("A", "AA", "A@gmail.com");
      Channel channel = Channel.of("name", "description");
      String content = "test";
      MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, content);
      MessageDto expectedDto = new MessageDto(null, null, null, content,
          null, null, List.of());

      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      given(userRepository.findById(authorId)).willReturn(Optional.of(user));
      given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

      // when
      MessageDto actualDto = messageService.createMessage(request, null);

      // then
      assertEquals(expectedDto.content(), actualDto.content());
      assertEquals(0, actualDto.attachments().size());
    }

    @Test
    @DisplayName("userId, channelId, 메세지와 첨부파일과 함께 messageDto 반환")
    void should_return_messageDto_when_fields_are_provided_with_attachments() throws IOException {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      Channel channel = Channel.of("name", "description");
      String content = "test";
      MessageCreateRequest request = new MessageCreateRequest(UUID.randomUUID(), UUID.randomUUID(),
          content);
      MessageDto expectedDto = new MessageDto(null, null, null, content, null,
          null, List.of(new BinaryContentDto(UUID.randomUUID(), null, 0, null)));
      MockMultipartFile mockFile = new MockMultipartFile(
          "test", "test_file_name", "test_content_Type", "test".getBytes());
      List<MultipartFile> files = List.of(mockFile);

      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
      given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

      // when
      MessageDto actualDto = messageService.createMessage(request, files);

      // then
      assertEquals(expectedDto.content(), actualDto.content());
      assertEquals(1, actualDto.attachments().size());
      then(binaryContentStorage).should(atLeastOnce()).put(any(), any());
    }

    @Test
    @DisplayName("채널이 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_channel_not_found() {
      // given
      MessageCreateRequest request = new MessageCreateRequest(UUID.randomUUID(), UUID.randomUUID(),
          "test");
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(ChannelNotFoundException.class,
          () -> messageService.createMessage(request, null));
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_user_not_found() {
      // given
      MessageCreateRequest request = new MessageCreateRequest(UUID.randomUUID(), UUID.randomUUID(),
          "test");
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(Channel.of(null)));
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(UserNotFoundException.class,
          () -> messageService.createMessage(request, null));
    }

  }

  @Nested
  class updateMessage {

    @Test
    @DisplayName("새로운 메세지 내용으로 바꾼 messageDto 반환")
    void should_return_messageDto_when_new_content_is_provided() {
      // given
      String newContent = "new content";
      Message existingMsg = new Message(
          Channel.of(null),
          new User(null, null, null),
          "abc");
      MessageDto expectedDto = new MessageDto(null, null, null, newContent,
          null, null, List.of());
      MessageUpdateRequest request = new MessageUpdateRequest(newContent);
      given(messageRepository.findById(any(UUID.class))).willReturn(Optional.of(existingMsg));
      given(messageMapper.toDto(existingMsg)).willReturn(expectedDto);

      // when, then
      MessageDto actualDto = messageService.updateMessage(UUID.randomUUID(), request);

      // then
      assertEquals(expectedDto.content(), actualDto.content());
      then(messageRepository).should().save(existingMsg);
    }

    @Test
    @DisplayName("메세지가 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_message_not_found() {
      // given
      MessageUpdateRequest request = new MessageUpdateRequest("new content");
      given(messageRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(MessageNotFoundException.class,
          () -> messageService.updateMessage(UUID.randomUUID(), request));
      then(messageRepository).should(never()).save(any());
    }

  }

  @Nested
  class deleteMessage {

    @Test
    @DisplayName("메세지가 존재할 경우 성공적으로 삭제")
    void should_delete_message_when_id_exists() throws IOException {
      // given
      Message msg = new Message(
          Channel.of(null),
          new User(null, null, null),
          null);

      given(messageRepository.findById(any(UUID.class))).willReturn(Optional.of(msg));

      // when
      messageService.deleteMessage(UUID.randomUUID());

      // then
      then(messageRepository).should().deleteById(any());
    }

    @Test
    @DisplayName("메세지가 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_message_not_found() {
      // given
      given(messageRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(MessageNotFoundException.class,
          () -> messageService.deleteMessage(UUID.randomUUID()));
    }
  }

  @Nested
  class findAllByChannelId {

    @Test
    @DisplayName("cursor가 null이고 next cursor가 있는 PageResponse<MessageDto> 반환")
    void should_return_page_with_next_cursor_when_cursor_is_null() {
      // given
      Pageable page = PageRequest.of(0, 2);   // pageSize: 2

      Message msg = mock(Message.class);
      List<Message> messages = List.of(mock(Message.class), mock(Message.class), msg);

      given(msg.getCreatedAt()).willReturn(Instant.now());
      given(messageRepository.findByChannelIdOrderByCreatedAtDesc(
          any(UUID.class), any(Pageable.class))).willReturn(messages);

      // when
      PageResponse<MessageDto> actualDto = messageService.findAllByChannelId(
          UUID.randomUUID(), null, page);

      // then
      ArgumentCaptor<Instant> cursorCaptor = ArgumentCaptor.forClass(Instant.class);
      ArgumentCaptor<Boolean> hasNextCaptor = ArgumentCaptor.forClass(Boolean.class);

      then(pageMapper).should()
          .fromData(anyList(), cursorCaptor.capture(), hasNextCaptor.capture());
      assertTrue(hasNextCaptor.getValue());
      assertEquals(msg.getCreatedAt(), cursorCaptor.getValue());

    }

    @Test
    @DisplayName("cursor가 null이 아니고 next cursor가 없는 PageResponse<MessageDto> 반환")
    void should_return_page_when_cursor_is_not_null() {
      // given
      Pageable page = PageRequest.of(0, 2);   // pageSize: 2

      // 마지막 문자만 남았다 가정
      List<Message> messages = List.of(mock(Message.class));

      given(messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          any(UUID.class), any(Instant.class), any(Pageable.class))).willReturn(messages);

      // when
      PageResponse<MessageDto> actualDto = messageService.findAllByChannelId(
          UUID.randomUUID(), Instant.now().minusSeconds(5), page);

      // then
      ArgumentCaptor<Instant> cursorCaptor = ArgumentCaptor.forClass(Instant.class);
      ArgumentCaptor<Boolean> hasNextCaptor = ArgumentCaptor.forClass(Boolean.class);

      then(pageMapper).should()
          .fromData(anyList(), cursorCaptor.capture(), hasNextCaptor.capture());
      assertFalse(hasNextCaptor.getValue());
      assertNull(cursorCaptor.getValue());

    }

    @Test
    @DisplayName("cursor가 null이 아니고 데이터 크기가 정확해 next cursor가 없는 PageResponse<MessageDto> 반환")
    void should_return_page_without_next_cursor_when_data_size_is_exact_and_cursor_is_not_null() {
      // given
      Pageable page = PageRequest.of(0, 2);   // pageSize: 2

      // 마지막 문자만 남았다 가정
      List<Message> messages = List.of(mock(Message.class), mock(Message.class));

      given(messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          any(UUID.class), any(Instant.class), any(Pageable.class))).willReturn(messages);

      // when
      PageResponse<MessageDto> actualDto = messageService.findAllByChannelId(
          UUID.randomUUID(), Instant.now().minusSeconds(5), page);

      // then
      ArgumentCaptor<Instant> cursorCaptor = ArgumentCaptor.forClass(Instant.class);
      ArgumentCaptor<Boolean> hasNextCaptor = ArgumentCaptor.forClass(Boolean.class);

      then(pageMapper).should()
          .fromData(anyList(), cursorCaptor.capture(), hasNextCaptor.capture());
      assertFalse(hasNextCaptor.getValue());
      assertNull(cursorCaptor.getValue());

    }

  }


}