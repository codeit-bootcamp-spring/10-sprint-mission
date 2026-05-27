package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.message.AttachmentsUploadFailedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.session.UserSessionManager;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    private MessageMapper messageMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private UserSessionManager userSessionManager;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Nested
    @DisplayName("메시지 생성 테스트")
    class createMessage {
        UUID authorId;
        User author;
        UserDto authorDto;
        UUID channelId;
        Channel channel;

        @BeforeEach
        void setUpCreateMessage() {
            authorId = UUID.randomUUID();
            author = new User("testUser@gmail.com", "testUser", "1234", null);
            ReflectionTestUtils.setField(author, "id", authorId);
            authorDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, true, author.getRole());

            channelId = UUID.randomUUID();
            channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);
        }

        @Test
        @DisplayName("첨부파일이 없는 메시지를 생성할 수 있다.")
        void success_create_message_without_attachments() {
            // given(준비)
            MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "testMessageContent입니다.");

            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            UUID messageId = UUID.randomUUID();
            Message message = new Message(channel, author, request.content());
            ReflectionTestUtils.setField(message, "id", messageId);
            MessageDto expectedMessageDto = new MessageDto(messageId, message.getCreatedAt(), message.getUpdatedAt(), message.getContent(), message.getChannel().getId(), authorDto, null);

            given(messageMapper.toDto(any(Message.class))).willReturn(expectedMessageDto);

            // when(실행)
            MessageDto result = basicMessageService.create(request, null);

            // then(검증)
            assertEquals(expectedMessageDto, result);
            assertEquals(expectedMessageDto.author(), result.author());
            assertEquals(expectedMessageDto.channelId(), result.channelId());
            assertEquals(expectedMessageDto.content(), result.content());

            verify(userRepository).findById(authorId);
            verify(channelRepository).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository).save(any(Message.class));
            verify(messageMapper).toDto(any(Message.class));
        }

        @Test
        @DisplayName("첨부파일이 있는 메시지를 생성할 수 있다.")
        void success_create_message_with_attachments() throws IOException {
            // given(준비)
            MultipartFile attachment1File = mock(MultipartFile.class);
            byte[] attachment1Bytes = "attachment1".getBytes();
            given(attachment1File.isEmpty()).willReturn(false);
            given(attachment1File.getBytes()).willReturn(attachment1Bytes);
            given(attachment1File.getOriginalFilename()).willReturn("attachment1");
            given(attachment1File.getContentType()).willReturn("image/png");

            MultipartFile attachment2File = mock(MultipartFile.class);
            byte[] attachment2Bytes = "attachment2".getBytes();
            given(attachment2File.isEmpty()).willReturn(false);
            given(attachment2File.getBytes()).willReturn(attachment2Bytes);
            given(attachment2File.getOriginalFilename()).willReturn("attachment2");
            given(attachment2File.getContentType()).willReturn("image/png");

            MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "testMessageContent입니다.");
            List<MultipartFile> attachments = List.of(attachment1File, attachment2File);

            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            UUID messageId = UUID.randomUUID();
            Message message = new Message(channel, author, request.content());
            ReflectionTestUtils.setField(message, "id", messageId);

            UUID attachment1Id = UUID.randomUUID();
            UUID attachment2Id = UUID.randomUUID();
            BinaryContent attachment1 = new BinaryContent(attachment1File.getOriginalFilename(), attachment1File.getContentType(), attachment1File.getSize());
            BinaryContent attachment2 = new BinaryContent(attachment2File.getOriginalFilename(), attachment2File.getContentType(), attachment2File.getSize());
            ReflectionTestUtils.setField(attachment1, "id", attachment1Id);
            ReflectionTestUtils.setField(attachment2, "id", attachment2Id);

            message.addAttachment(attachment1);
            message.addAttachment(attachment2);

            BinaryContentDto attachment1Dto = new BinaryContentDto(attachment1Id, attachment1.getFileName(), attachment1.getSize(), attachment1.getContentType());
            BinaryContentDto attachment2Dto = new BinaryContentDto(attachment2Id, attachment2.getFileName(), attachment2.getSize(), attachment2.getContentType());

            MessageDto expectedMessageDto = new MessageDto(messageId, message.getCreatedAt(), message.getUpdatedAt(), message.getContent(), message.getChannel().getId(), authorDto, List.of(attachment1Dto, attachment2Dto));

            given(messageMapper.toDto(any(Message.class))).willReturn(expectedMessageDto);

            // when(실행)
            MessageDto result = basicMessageService.create(request, attachments);

            // then(검증)
            assertEquals(expectedMessageDto, result);
            assertEquals(expectedMessageDto.author(), result.author());
            assertEquals(expectedMessageDto.channelId(), result.channelId());
            assertEquals(expectedMessageDto.content(), result.content());

            verify(userRepository).findById(authorId);
            verify(channelRepository).findById(channelId);
            verify(binaryContentRepository, times(2)).save(any(BinaryContent.class));
            verify(binaryContentStorage, times(2)).put(any(), any());
            verify(messageRepository).save(any(Message.class));
            verify(messageMapper).toDto(any(Message.class));
        }

        @Test
        @DisplayName("작성자 ID가 null이면 예외가 발생한다.")
        void fail_create_message_when_authorId_null() {
            // given(준비
            MessageCreateRequest request = new MessageCreateRequest(null, channelId, "testMessageContent입니다.");

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.create(request, null));

            verify(userRepository, never()).findById(authorId);
            verify(channelRepository, never()).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository, never()).save(any(Message.class));
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        @Test
        @DisplayName("특정 ID로 작성자를 찾을 수 없으면 예외가 발생한다.")
        void fail_create_message_when_author_not_found() {
            // given(준비
            MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "testMessageContent입니다.");

            given(userRepository.findById(authorId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicMessageService.create(request, null));

            verify(userRepository).findById(authorId);
            verify(channelRepository, never()).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository, never()).save(any(Message.class));
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        @Test
        @DisplayName("채널 ID가 null이면 예외가 발생한다.")
        void fail_create_message_when_channelId_null() {
            // given(준비
            MessageCreateRequest request = new MessageCreateRequest(authorId, null, "testMessageContent입니다.");

            given(userRepository.findById(authorId)).willReturn(Optional.of(author));

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.create(request, null));

            verify(userRepository).findById(authorId);
            verify(channelRepository, never()).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository, never()).save(any(Message.class));
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        @Test
        @DisplayName("특정 ID로 채널을 찾을 수 없으면 예외가 발생한다.")
        void fail_create_message_when_channel_not_found() {
            // given(준비
            MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "testMessageContent입니다.");

            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicMessageService.create(request, null));

            verify(userRepository).findById(authorId);
            verify(channelRepository).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository, never()).save(any(Message.class));
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        // AttachmentsUploadFailedException
        @Test
        @DisplayName("첨부파일 업로드를 실패하면 예외가 발생한다.")
        void fail_create_message_when_attachment_upload_fail() throws IOException {
            // given(준비)
            MultipartFile attachment1File = mock(MultipartFile.class);
            given(attachment1File.isEmpty()).willReturn(false);
            given(attachment1File.getBytes()).willThrow(new IOException("파일 업로드 실패"));

            MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "testMessageContent입니다.");
            List<MultipartFile> attachments = List.of(attachment1File);

            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            UUID messageId = UUID.randomUUID();
            Message message = new Message(channel, author, request.content());
            ReflectionTestUtils.setField(message, "id", messageId);

            // when(실행), then(검증)
            assertThrows(AttachmentsUploadFailedException.class,
                    () -> basicMessageService.create(request, attachments));

            verify(userRepository).findById(authorId);
            verify(channelRepository).findById(channelId);

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(messageRepository, never()).save(any(Message.class));
            verify(messageMapper, never()).toDto(any(Message.class));
        }
    }

    @Nested
    @DisplayName("메시지 목록 조회 테스트")
    class findMessageList {
        UUID authorId;
        User author;
        UserDto authorDto;

        @BeforeEach
        void setUpCreateMessage() {
            authorId = UUID.randomUUID();
            author = new User("testUser@gmail.com", "testUser", "1234", null);
            ReflectionTestUtils.setField(author, "id", authorId);
            authorDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, true, author.getRole());
        }

        @Test
        @DisplayName("특정 채널의 전체 메시지 목록을 페이지네이션으로 조회할 수 있다.")
        void success_find_message_list() {
            // given(준비)
            Instant cursor = Instant.now();
            Pageable pageable = PageRequest.of(0, 2);

            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            given(userSessionManager.getOnlineUserIds()).willReturn(Set.of());

            UUID messageId1 = UUID.randomUUID();
            UUID messageId2 = UUID.randomUUID();
            UUID messageId3 = UUID.randomUUID();

            Instant createdAt1 = Instant.parse("2026-03-30T10:00:00Z");
            Instant createdAt2 = Instant.parse("2026-03-30T09:00:00Z");
            Instant createdAt3 = Instant.parse("2026-03-30T08:00:00Z");

            Message message1 = new Message(channel, author, "message1");
            Message message2 = new Message(channel, author, "message2");
            Message message3 = new Message(channel, author, "message3");

            ReflectionTestUtils.setField(message1, "id", messageId1);
            ReflectionTestUtils.setField(message2, "id", messageId2);
            ReflectionTestUtils.setField(message3, "id", messageId3);

            ReflectionTestUtils.setField(message1, "createdAt", createdAt1);
            ReflectionTestUtils.setField(message2, "createdAt", createdAt2);
            ReflectionTestUtils.setField(message3, "createdAt", createdAt3);

            Slice<Message> messageSlice = new SliceImpl<>(List.of(message1, message2, message3), pageable, true);

            given(messageRepository.findAllByChannelId(channelId, cursor, pageable)).willReturn(messageSlice);


            MessageDto messageDto1 = new MessageDto(messageId1, message1.getCreatedAt(), null, message1.getContent(), message1.getChannel().getId(), authorDto, null);
            MessageDto messageDto2 = new MessageDto(messageId2, message2.getCreatedAt(), null, message2.getContent(), message2.getChannel().getId(), authorDto, null);
            MessageDto messageDto3 = new MessageDto(messageId3, message3.getCreatedAt(), null, message3.getContent(), message3.getChannel().getId(), authorDto, null);

            given(messageMapper.toDto(message1, Set.of())).willReturn(messageDto1);
            given(messageMapper.toDto(message2, Set.of())).willReturn(messageDto2);
            given(messageMapper.toDto(message3, Set.of())).willReturn(messageDto3);

            Slice<MessageDto> messageDtoSlice = new SliceImpl<>(List.of(messageDto1, messageDto2, messageDto3), pageable, true);

            PageResponse<MessageDto> expectedMessageDtoPageResponse = new PageResponse<>(messageDtoSlice.getContent(), createdAt3, messageDtoSlice.getSize(), messageDtoSlice.hasNext(), null);
            given(pageResponseMapper.fromSlice(Mockito.<Slice<MessageDto>>any(), eq(createdAt3))).willReturn(expectedMessageDtoPageResponse);


            // when(실행)
            PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, cursor, pageable);

            // then(검증)
            assertEquals(expectedMessageDtoPageResponse, result);
            assertEquals(3, result.content().size());

            verify(channelRepository).findById(channelId);
            verify(messageRepository).findAllByChannelId(channelId, cursor, pageable);

            verify(messageMapper, times(3)).toDto(any(Message.class), anySet());
            verify(pageResponseMapper).fromSlice(Mockito.<Slice<MessageDto>>any(), eq(createdAt3));
        }

        @Test
        @DisplayName("특정 채널에 메시지가 없을 때 목록 조회 시 빈 메시지 목록을 출력할 수 있다.")
        void success_find_empty_message_list() {
            // given(준비)
            Instant cursor = Instant.now();
            Pageable pageable = PageRequest.of(0, 2);

            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(userSessionManager.getOnlineUserIds()).willReturn(Set.of());

            Slice<Message> messageSlice = new SliceImpl<>(List.of(), pageable, false);

            given(messageRepository.findAllByChannelId(channelId, cursor, pageable)).willReturn(messageSlice);

            Slice<MessageDto> messageDtoSlice = new SliceImpl<>(List.of(), pageable, false);
            PageResponse<MessageDto> expectedMessageDtoPageResponse = new PageResponse<>(messageDtoSlice.getContent(), null, messageDtoSlice.getSize(), messageDtoSlice.hasNext(), null);

            given(pageResponseMapper.fromSlice(Mockito.<Slice<MessageDto>>any(), eq(null))).willReturn(expectedMessageDtoPageResponse);

            // when(실행)
            PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, cursor, pageable);

            // then(검증)
            assertEquals(expectedMessageDtoPageResponse, result);
            assertEquals(0, result.content().size());

            verify(channelRepository).findById(channelId);
            verify(messageRepository).findAllByChannelId(channelId, cursor, pageable);

            verify(messageMapper, never()).toDto(any(Message.class), anySet());
            verify(pageResponseMapper).fromSlice(Mockito.<Slice<MessageDto>>any(), eq(null));
        }

        @Test
        @DisplayName("채널 ID가 null이면 예외가 발생한다.")
        void fail_find_message_list_when_channelId_null() {
            // given(준비)
            Instant cursor = Instant.now();
            Pageable pageable = PageRequest.of(0, 2);

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.findAllByChannelId(null, cursor, pageable));

            verify(channelRepository, never()).findById(any());
            verify(messageRepository, never()).findAllByChannelId(any(), any(), any());

            verify(messageMapper, never()).toDto(any(Message.class));
            verify(pageResponseMapper, never()).fromSlice(Mockito.<Slice<MessageDto>>any(), any());
        }

        @Test
        @DisplayName("해당 채널 ID를 가진 채널이 없으면 예외가 발생한다.")
        void fail_find_message_list_when_channel_not_found() {
            // given(준비)
            Instant cursor = Instant.now();
            Pageable pageable = PageRequest.of(0, 2);

            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicMessageService.findAllByChannelId(channelId, cursor, pageable));

            verify(channelRepository).findById(channelId);
            verify(messageRepository, never()).findAllByChannelId(any(), any(), any());

            verify(messageMapper, never()).toDto(any(Message.class));
            verify(pageResponseMapper, never()).fromSlice(Mockito.<Slice<MessageDto>>any(), any());
        }
    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class updateMessage {
        UUID authorId;
        User author;
        UserDto authorDto;
        UUID channelId;
        Channel channel;

        @BeforeEach
        void setUpCreateMessage() {
            authorId = UUID.randomUUID();
            author = new User("testUser@gmail.com", "testUser", "1234", null);
            ReflectionTestUtils.setField(author, "id", authorId);
            authorDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, true, author.getRole());

            channelId = UUID.randomUUID();
            channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);
        }

        @Test
        @DisplayName("메시지 ID로 메시지를 수정할 수 있다.")
        void success_update_message() {
            // given(준비)
            MessageUpdateRequest request = new MessageUpdateRequest("수정된 testMessageContent입니다.");

            UUID messageId = UUID.randomUUID();
            Message message = new Message(channel, author, "testMessageContent입니다.");
            ReflectionTestUtils.setField(message, "id", messageId);

            MessageDto expectedMessageDto = new MessageDto(messageId, message.getCreatedAt(), message.getUpdatedAt(), message.getContent(), message.getChannel().getId(), authorDto, null);

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.of(message));
            given(messageMapper.toDto(any(Message.class))).willReturn(expectedMessageDto);

            // when(실행)
            MessageDto result = basicMessageService.update(messageId, request);

            // then(검증)
            assertEquals(expectedMessageDto, result);

            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(messageMapper).toDto(any(Message.class));
        }

        @Test
        @DisplayName("메시지 ID가 null이면 예외가 발생한다.")
        void fail_update_message_when_messageId_null() {
            // given(준비)
            MessageUpdateRequest request = new MessageUpdateRequest("수정된 testMessageContent입니다.");

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.update(null, request));

            verify(messageRepository, never()).findByIdWithAuthorAndChannel(any());
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        @Test
        @DisplayName("메시지 ID로 메시지를 찾을 수 없으면 예외가 발생한다.")
        void fail_update_message_when_message_not_found() {
            // given(준비)
            MessageUpdateRequest request = new MessageUpdateRequest("수정된 testMessageContent입니다.");

            UUID messageId = UUID.randomUUID();

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(MessageNotFoundException.class,
                    () -> basicMessageService.update(messageId, request));

            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(messageMapper, never()).toDto(any(Message.class));
        }

        @Test
        @DisplayName("입력된 메시지 내용이 null이면 예외가 발생한다.")
        void fail_update_message_when_message_content_null() {
            // given(준비)
            MessageUpdateRequest request = new MessageUpdateRequest(null);

            UUID messageId = UUID.randomUUID();
            Message message = new Message(channel, author, request.newContent());

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.of(message));

            // when(실행), then(검증)
            assertThrows(NoChangeValueException.class,
                    () -> basicMessageService.update(messageId, request));

            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(messageMapper, never()).toDto(any(Message.class));
        }
    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class deleteMessage {
        UUID authorId;
        User author;
        UUID messageId;

        @BeforeEach
        void setUpDeleteMessage() {
            authorId = UUID.randomUUID();
            author = new User("testUser@gmail.com", "testUser", "1234", null);
            ReflectionTestUtils.setField(author, "id", authorId);

            messageId = UUID.randomUUID();
        }

        @Test
        @DisplayName("메시지 ID로 첨부파일이 없는 메시지를 삭제할 수 있다.")
        void success_delete_message_without_attachments() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            Message message = new Message(channel, author, "testMessageContent입니다.");
            ReflectionTestUtils.setField(message, "id", messageId);

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn((Optional.of(message)));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when(실행)
            basicMessageService.delete(messageId);

            // then(검증)
            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(channelRepository).findById(channelId);

            verify(messageRepository).deleteById(messageId);
        }

        @Test
        @DisplayName("메시지 ID로 첨부파일이 있는 메시지를 삭제할 수 있다.")
        void success_delete_message_with_attachments() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            Message message = new Message(channel, author, "testMessageContent입니다.");
            ReflectionTestUtils.setField(message, "id", messageId);

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn((Optional.of(message)));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            BinaryContent attachment1 = mock(BinaryContent.class);
            BinaryContent attachment2 = mock(BinaryContent.class);
            message.addAttachment(attachment1);
            message.addAttachment(attachment2);

            // when(실행)
            basicMessageService.delete(messageId);

            // then(검증)
            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(channelRepository).findById(channelId);

            verify(binaryContentRepository).deleteAll(message.getAttachments());
            verify(messageRepository).deleteById(messageId);
        }

        @Test
        @DisplayName("메시지 ID가 null이면 예외가 발생한다.")
        void fail_delete_message_when_messageId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.delete(null));

            verify(messageRepository, never()).findByIdWithAuthorAndChannel(null);
            verify(channelRepository, never()).findById(any());

            verify(binaryContentRepository, never()).deleteAll(anyList());
            verify(messageRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("해당 ID로 메시지를 찾을 없으면 예외가 발생한다.")
        void fail_delete_message_when_message_not_found() {
            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(MessageNotFoundException.class,
                    () -> basicMessageService.delete(messageId));

            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(channelRepository, never()).findById(any());

            verify(binaryContentRepository, never()).deleteAll(anyList());
            verify(messageRepository, never()).deleteById(any());

        }

        @Test
        @DisplayName("채널 ID가 null이면 예외가 발생한다.")
        void fail_delete_message_when_channelId_null() {
            // given(준비)
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", null);

            Message message = new Message(channel, author, "testMessageContent입니다.");
            ReflectionTestUtils.setField(message, "id", messageId);

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.of(message));

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicMessageService.delete(messageId));


            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(channelRepository, never()).findById(any());

            verify(binaryContentRepository, never()).deleteAll(anyList());
            verify(messageRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("해당 ID로 채널을 찾을 없으면 예외가 발생한다.")
        void fail_delete_message_when_channel_not_found() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "channelName", "channelDescription");
            ReflectionTestUtils.setField(channel, "id", channelId);

            Message message = new Message(channel, author, "testMessageContent입니다.");
            ReflectionTestUtils.setField(message, "id", messageId);

            given(messageRepository.findByIdWithAuthorAndChannel(messageId)).willReturn(Optional.of(message));
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicMessageService.delete(messageId));


            verify(messageRepository).findByIdWithAuthorAndChannel(messageId);
            verify(channelRepository).findById(channelId);

            verify(binaryContentRepository, never()).deleteAll(anyList());
            verify(messageRepository, never()).deleteById(any());
        }

    }
}