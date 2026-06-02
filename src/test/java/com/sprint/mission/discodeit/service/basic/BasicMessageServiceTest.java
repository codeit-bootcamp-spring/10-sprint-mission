package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageAccessDeniedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicMessageService 단위 테스트")
class BasicMessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private Channel publicChannel;
    private User author;
    private Message message;
    private MessageDto.Response messageResponse;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        author = new User("tester", "test@test.com", "pw", null);
        ReflectionTestUtils.setField(author, "id", authorId);

        publicChannel = new Channel(ChannelType.PUBLIC, "General", null);
        ReflectionTestUtils.setField(publicChannel, "id", channelId);

        message = new Message("Hello", publicChannel, author, List.of());
        ReflectionTestUtils.setField(message, "id", messageId);
        ReflectionTestUtils.setField(message, "createdAt", Instant.now());

        UserDto.Response authorResponse = new UserDto.Response(authorId, "tester", "test@test.com", null, true, Role.USER);
        messageResponse = new MessageDto.Response(messageId, Instant.now(), Instant.now(), "Hello", channelId, authorResponse, List.of());
    }

    @Nested
    @DisplayName("메시지 생성 (create)")
    class Create {
        @Test
        @DisplayName("성공: 공개 채널 메시지 생성")
        void create_Success_PublicChannel() {
            // given
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(messageRepository.save(any())).willReturn(message);
            given(messageMapper.toResponse(any())).willReturn(messageResponse);

            // when
            messageService.create(request, null);

            // then
            verify(messageRepository).save(any());
            verify(channelRepository).findById(channelId);
        }

        @Test
        @DisplayName("성공: 첨부파일이 빈 목록([])인 경우 정상 생성")
        void create_Success_EmptyAttachments() {
            // given
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(messageRepository.save(any())).willReturn(message);
            given(messageMapper.toResponse(any())).willReturn(messageResponse);

            // when
            messageService.create(request, Collections.emptyList());

            // then
            verify(messageRepository).save(any());
        }

        @Test
        @DisplayName("성공: 비공개 채널 메시지 생성 (참여자임)")
        void create_Success_PrivateChannel() {
            // given
            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
            ReflectionTestUtils.setField(privateChannel, "id", channelId);
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(readStatusRepository.existsByUserIdAndChannelId(authorId, channelId)).willReturn(true);
            given(messageRepository.save(any())).willReturn(message);
            given(messageMapper.toResponse(any())).willReturn(messageResponse);

            // when
            messageService.create(request, null);

            // then
            verify(readStatusRepository).existsByUserIdAndChannelId(authorId, channelId);
        }

        @Test
        @DisplayName("실패: 비공개 채널 메시지 생성 - 참여자가 아님")
        void create_Fail_AccessDenied() {
            // given
            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
            ReflectionTestUtils.setField(privateChannel, "id", channelId);
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(readStatusRepository.existsByUserIdAndChannelId(authorId, channelId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> messageService.create(request, null))
                    .isInstanceOf(MessageAccessDeniedException.class);
        }

        @Test
        @DisplayName("실패: 첨부파일 중 일부가 존재하지 않음")
        void create_Fail_AttachmentMissing() {
            // given
            UUID attachmentId = UUID.randomUUID();
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(binaryContentRepository.findAllById(any())).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> messageService.create(request, List.of(attachmentId)))
                    .isInstanceOf(BinaryContentNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 채널")
        void create_Fail_ChannelNotFound() {
            // given
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());
            MessageDto.CreateRequest request = new MessageDto.CreateRequest("Hello", authorId, channelId);

            // when & then
            assertThatThrownBy(() -> messageService.create(request, null))
                    .isInstanceOf(ChannelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("메시지 조회 (find/findAllByChannelId)")
    class Read {
        @Test
        @DisplayName("성공: 단건 조회")
        void find_Success() {
            // given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageMapper.toResponse(any())).willReturn(messageResponse);

            // when
            MessageDto.Response result = messageService.find(messageId);

            // then
            assertThat(result.id()).isEqualTo(messageId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 메시지 ID")
        void find_Fail_NotFound() {
            // given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.find(messageId))
                    .isInstanceOf(MessageNotFoundException.class);
        }

        @Test
        @DisplayName("성공: 커서 없이 채널의 최신 메시지 목록 조회")
        void findAllByChannelId_Success_NoCursor() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Slice<Message> messageSlice = new PageImpl<>(List.of(message), pageable, 1);
            PageResponse<MessageDto.Response> pageResponse = new PageResponse<>(List.of(messageResponse), "next-cursor", 10, true, 1L);

            given(channelRepository.existsById(channelId)).willReturn(true);
            given(messageRepository.findLatestByChannelId(eq(channelId), any())).willReturn(messageSlice);
            given(pageResponseMapper.fromSlice(any(), any())).willReturn((PageResponse) pageResponse);

            // when
            messageService.findAllByChannelId(channelId, null, pageable);

            // then
            verify(messageRepository).findLatestByChannelId(eq(channelId), any());
        }

        @Test
        @DisplayName("성공: 커서를 이용한 이전 메시지 목록 조회")
        void findAllByChannelId_Success_WithCursor() {
            // given
            Instant cursor = Instant.now();
            Pageable pageable = PageRequest.of(0, 10);
            Slice<Message> messageSlice = new PageImpl<>(List.of(message), pageable, 1);
            PageResponse<MessageDto.Response> pageResponse = new PageResponse<>(List.of(messageResponse), null, 10, false, 1L);

            given(channelRepository.existsById(channelId)).willReturn(true);
            given(messageRepository.findAllUseCursorByChannelId(eq(channelId), eq(cursor), any())).willReturn(messageSlice); 
            given(pageResponseMapper.fromSlice(any(), any())).willReturn((PageResponse) pageResponse);

            // when
            messageService.findAllByChannelId(channelId, cursor, pageable);

            // then
            verify(messageRepository).findAllUseCursorByChannelId(eq(channelId), eq(cursor), any());
        }

        @Test
        @DisplayName("성공: 마지막 페이지 조회 시 nextCursor가 null임")
        void findAllByChannelId_LastPage_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Slice<Message> messageSlice = new PageImpl<>(List.of(message), pageable, 0); 
            PageResponse<MessageDto.Response> pageResponse = new PageResponse<>(List.of(messageResponse), null, 10, false, 1L);

            given(channelRepository.existsById(channelId)).willReturn(true);
            given(messageRepository.findLatestByChannelId(eq(channelId), any())).willReturn(messageSlice);
            given(pageResponseMapper.fromSlice(any(), isNull())).willReturn((PageResponse) pageResponse);

            // when
            PageResponse<MessageDto.Response> result = messageService.findAllByChannelId(channelId, null, pageable);

            // then
            assertThat(result.nextCursor()).isNull();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 채널 조회")
        void findAllByChannelId_Fail_NotFound() {
            // given
            given(channelRepository.existsById(channelId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, null, PageRequest.of(0, 10)))
                    .isInstanceOf(ChannelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("메시지 수정 (update)")
    class Update {
        @Test
        @DisplayName("성공: 메시지 내용 수정")
        void update_Success() {
            // given
            MessageDto.UpdateRequest request = new MessageDto.UpdateRequest("New Content");
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageMapper.toResponse(any())).willReturn(messageResponse);

            // when
            messageService.update(messageId, request);

            // then
            assertThat(message.getContent()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 메시지 수정")
        void update_Fail_NotFound() {
            // given
            MessageDto.UpdateRequest request = new MessageDto.UpdateRequest("New Content");
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.update(messageId, request))
                    .isInstanceOf(MessageNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("메시지 삭제 (delete)")
    class Delete {
        @Test
        @DisplayName("성공: 메시지 삭제 후 이전 메시지가 존재하여 채널의 lastMessageAt이 업데이트됨")
        void delete_Success_LastMessageRemaining() {
            // given
            Instant remainingMessageTime = Instant.now().minusSeconds(10);
            Message remainingMessage = mock(Message.class);
            given(remainingMessage.getCreatedAt()).willReturn(remainingMessageTime);

            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId))
                    .willReturn(Optional.of(remainingMessage));

            // when
            messageService.delete(messageId);

            // then
            verify(messageRepository).delete(message);
            assertThat(publicChannel.getLastMessageAt()).isEqualTo(remainingMessageTime);
        }

        @Test
        @DisplayName("성공: 마지막 메시지 삭제 후 남은 메시지가 없어 lastMessageAt이 null이 됨")
        void delete_Success_NoMessageRemaining() {
            // given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId))
                    .willReturn(Optional.empty());

            // when
            messageService.delete(messageId);

            // then
            verify(messageRepository).delete(message);
            assertThat(publicChannel.getLastMessageAt()).isNull();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 메시지 삭제")
        void delete_Fail_NotFound() {
            // given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> messageService.delete(messageId))
                    .isInstanceOf(MessageNotFoundException.class);
        }
    }
}
