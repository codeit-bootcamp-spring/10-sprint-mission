package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelAccessDeniedException;
import com.sprint.mission.discodeit.exception.message.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicMessageService messageService;

  @Nested
  @DisplayName("메시지 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: PUBLIC 채널에 파일 없이 메시지를 생성한다")
    void create_Success_PublicChannel_NoFiles() {
      // given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      UUID messageId = UUID.randomUUID();
      String content = "공개 채널 메시지";

      User mockUser = new User("tester", "test@test.com", "pw", null);
      Channel mockChannel = new Channel("공개 채널", "공개 채널 설명", ChannelType.PUBLIC);
      Message mockMessage = new Message(content, mockUser, mockChannel, null);

      org.springframework.test.util.ReflectionTestUtils.setField(mockMessage, "id", messageId);

      given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

      given(messageRepository.save(any(Message.class))).willReturn(mockMessage);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(Optional.of(mockMessage));

      // when
      Message result = messageService.create(content, authorId, channelId, null);

      // then
      assertThat(result.getContent()).isEqualTo(content);
      then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("성공: PRIVATE 채널 권한이 있는 유저가 첨부파일과 함께 메시지를 생성한다")
    void create_Success_PrivateChannel_WithFiles() throws IOException {
      // given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      UUID messageId = UUID.randomUUID();
      String content = "비공개 채널 메시지";

      MultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png",
          "data".getBytes());
      List<MultipartFile> attachments = List.of(mockFile);

      User mockUser = new User("tester", "test@test.com", "pw", null);
      Channel mockChannel = new Channel("비공개 채널", "비공개 채널 설명", ChannelType.PRIVATE);

      // 엔티티에 ID 세팅
      org.springframework.test.util.ReflectionTestUtils.setField(mockUser, "id", authorId);
      org.springframework.test.util.ReflectionTestUtils.setField(mockChannel, "id", channelId);

      ReadStatus mockReadStatus = new ReadStatus(mockUser, mockChannel, Instant.now());
      Message mockMessage = new Message(content, mockUser, mockChannel,
          List.of(new BinaryContent("test.png", 10L, "image/png")));

      org.springframework.test.util.ReflectionTestUtils.setField(mockMessage, "id", messageId);

      given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
      // PRIVATE 권한 체크 통과 설정
      given(readStatusRepository.findByUserIdAndChannelId(authorId, channelId)).willReturn(
          Optional.of(mockReadStatus));

      // 파일 저장 설정
      given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(
          new BinaryContent("test.png", 10L, "image/png"));
      given(messageRepository.save(any(Message.class))).willReturn(mockMessage);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(Optional.of(mockMessage));

      // when
      Message result = messageService.create(content, authorId, channelId, attachments);

      // then
      assertThat(result.getContent()).isEqualTo(content);
      then(binaryContentStorage).should().put(any(), any()); // 스토리지에 파일 저장 확인
    }

    @Test
    @DisplayName("실패: PRIVATE 채널에 참여하지 않은 유저가 메시지를 쓰려하면 예외 발생")
    void create_Fail_ChannelAccessDenied() {
      // given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();

      User mockUser = new User("tester", "test@test.com", "pw", null);
      Channel mockChannel = new Channel("비밀방", "설명", ChannelType.PRIVATE);

      org.springframework.test.util.ReflectionTestUtils.setField(mockChannel, "id",
          channelId); // 엔티티에 ID 세팅

      given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

      // 권한 없음 설정
      given(readStatusRepository.findByUserIdAndChannelId(authorId, channelId)).willReturn(
          Optional.empty());

      // when & then
      assertThrows(ChannelAccessDeniedException.class, () -> {
        messageService.create("내용", authorId, channelId, null);
      });
    }
  }

  @Nested
  @DisplayName("메시지 목록 조회 테스트")
  class FindAllByChannelIdTest {

    @Test
    @DisplayName("성공: 채널의 메시지 목록을 Slice 형태로 페이징하여 가져온다")
    void findAllByChannelId_Success() {
      // given
      UUID channelId = UUID.randomUUID();
      Instant cursor = Instant.now();
      Pageable pageable = PageRequest.of(0, 10);

      Message msg = new Message("메시지", new User("tester", "t@t.com", "pw", null),
          new Channel("방", "방", ChannelType.PUBLIC), null);
      Slice<Message> mockSlice = new SliceImpl<>(List.of(msg), pageable, false); // 가짜 슬라이스 객체

      given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class),
          eq(pageable)))
          .willReturn(mockSlice);

      // when
      Slice<Message> result = messageService.findAllByChannelId(channelId, cursor, pageable);

      // then
      assertThat(result.getContent().size()).isEqualTo(1);
      assertThat(result.getContent().get(0).getContent()).isEqualTo("메시지");
    }
  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 존재하는 메시지의 텍스트 내용을 수정한다")
    void update_Success() {
      // given
      UUID messageId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      String newContent = "수정된 내용";

      User author = new User("작성자", "test@test.com", "pw", null);
      org.springframework.test.util.ReflectionTestUtils.setField(author, "id",
          authorId); // 엔티티에 ID 세팅

      Message existingMessage = new Message("원래 내용", author, null, null);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(
          Optional.of(existingMessage));

      // when
      Message result = messageService.update(messageId, authorId, newContent);

      // then
      assertThat(result.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 메시지 수정 시 MessageNotFoundException 발생")
    void update_Fail_NotFound() {
      UUID messageId = UUID.randomUUID();
      UUID requesterId = UUID.randomUUID();
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(Optional.empty());

      assertThrows(MessageNotFoundException.class, () -> {
        messageService.update(messageId, requesterId, "새 내용");
      });
    }

    @Test
    @DisplayName("실패: 자신이 작성하지 않은 메시지를 수정하려 하면 MessageAccessDeniedException 발생")
    void update_Fail_AccessDenied() {
      // given
      UUID messageId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      UUID not_authorId = UUID.randomUUID(); // 다른 사람의 ID

      User author = new User("작성자", "test@test.com", "pw", null);
      org.springframework.test.util.ReflectionTestUtils.setField(author, "id",
          authorId); // 엔티티에 ID 세팅

      Message existingMessage = new Message("원본 내용", author, null, null);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(
          Optional.of(existingMessage));

      // when & then
      assertThrows(MessageAccessDeniedException.class, () -> {
        // 작성자가 아닌 유저 ID로 수정 요청
        messageService.update(messageId, not_authorId, "수정된 내용");
      });
    }
  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 존재하는 메시지를 삭제한다")
    void delete_Success() {
      // given
      UUID messageId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();

      User author = new User("작성자", "test@test.com", "pw", null);
      org.springframework.test.util.ReflectionTestUtils.setField(author, "id",
          authorId); // 엔티티에 ID 세팅

      Message existingMessage = new Message("삭제할 내용", author, null, null);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(
          Optional.of(existingMessage));

      // when
      messageService.deleteById(messageId, authorId);

      // then
      then(messageRepository).should().delete(existingMessage);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 메시지 삭제 시 MessageNotFoundException 발생")
    void delete_Fail_NotFound() {
      UUID messageId = UUID.randomUUID();
      UUID requesterId = UUID.randomUUID();
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(Optional.empty());

      assertThrows(MessageNotFoundException.class, () -> {
        messageService.deleteById(messageId, requesterId);
      });
      then(messageRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("실패: 자신이 작성하지 않은 메시지를 삭제하려 하면 MessageAccessDeniedException 발생")
    void delete_Fail_AccessDenied() {
      // given
      UUID messageId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      UUID not_authorId = UUID.randomUUID(); // 다른 사람의 ID

      User author = new User("작성자", "test@test.com", "pw", null);
      org.springframework.test.util.ReflectionTestUtils.setField(author, "id", authorId);

      Message existingMessage = new Message("원본 내용", author, null, null);
      given(messageRepository.findByIdWithDetails(messageId)).willReturn(
          Optional.of(existingMessage));

      // when & then
      assertThrows(MessageAccessDeniedException.class, () -> {
        messageService.deleteById(messageId, not_authorId);
      });
      then(messageRepository).should(never()).delete(any());
    }
  }
}