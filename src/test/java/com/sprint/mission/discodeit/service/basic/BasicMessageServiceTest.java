package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
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
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    ChannelRepository channelRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MessageMapper messageMapper;
    @Mock
    BinaryContentRepository binaryContentRepository;
    @Mock
    BinaryContentStorage binaryContentStorage;
    @Mock
    PageResponseMapper pageResponseMapper;

    @InjectMocks
    BasicMessageService messageService;


    @Nested
    public class message_create_test {

        @Test
        @DisplayName("메세지 생성 성공 (프로필 X)")
        void message_create_success() {
            // given
            String content = "Hello";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();

            MessageCreateRequestDTO req = new MessageCreateRequestDTO(content, channelId, authorId);

            Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
            channel.setId(channelId);

            User author = new User("abc", "abc@a.com", "abc", null);
            author.setId(authorId);

            UserDto userDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(),
                null, false);

            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

            Message message = new Message(content, channel, author, null);
            message.setId(messageId);
            MessageDto expected = new MessageDto(messageId, createdAt, updatedAt, content,
                channel.getId(), userDto, null);

            when(messageRepository.save(any(Message.class))).thenReturn(message);
            when(messageMapper.toDto(any(Message.class))).thenReturn(expected);

            // when
            MessageDto result = messageService.create(null, req);

            // then
            assertEquals(expected, result);
            verify(channelRepository).findById(any(UUID.class));
            verify(userRepository).findById(any(UUID.class));
            verify(messageRepository).save(any(Message.class));


        }

        @Test
        @DisplayName("메세지 생성 성공 (프로필 O)")
        void message_create_with_profile_success() {
            // given
            String content = "Hello";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();
            UUID binaryContentId = UUID.randomUUID();

            MessageCreateRequestDTO req = new MessageCreateRequestDTO(content, channelId, authorId);

            Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
            channel.setId(channelId);

            User author = new User("abc", "abc@a.com", "abc", null);
            author.setId(authorId);

            UserDto userDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(),
                null, false);

            // 첨부 파일
            String fileName = "image.png";
            byte[] bytes = "dummy-data-bytes".getBytes(StandardCharsets.UTF_8);
            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                "Image/png");
            binaryContent.setId(binaryContentId);

            MultipartFile profile = org.mockito.Mockito.mock(MultipartFile.class);
            when(profile.getOriginalFilename()).thenReturn(fileName);
            when(profile.getSize()).thenReturn((long) bytes.length);
            when(profile.getContentType()).thenReturn("Image/png");
            try {
                when(profile.getBytes()).thenReturn(bytes);
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }

            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
            when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(binaryContent);

            Message message = new Message(content, channel, author, List.of(binaryContent));
            message.setId(messageId);
            MessageDto expected = new MessageDto(messageId, createdAt, updatedAt, content,
                channel.getId(), userDto, null);

            when(messageRepository.save(any(Message.class))).thenReturn(message);
            when(messageMapper.toDto(any(Message.class))).thenReturn(expected);

            // when
            MessageDto result = messageService.create(List.of(profile), req);

            // then
            assertNotNull(result);
            assertEquals(expected, result);
            assertEquals(expected.author().id(), authorId);
            assertEquals(expected.channelId(), channelId);
            verify(channelRepository).findById(any(UUID.class));
            verify(userRepository).findById(any(UUID.class));
            verify(binaryContentRepository).save(any(BinaryContent.class));
            verify(binaryContentStorage).put(eq(binaryContentId), eq(bytes));
            verify(messageRepository).save(any(Message.class));
        }
    }

    @Nested
    public class message_update_test {

        @Test
        @DisplayName("메세지 수정 성공")
        void message_update_success() {
            // given
            UUID messageId = UUID.randomUUID();
            String oldContent = "old";
            String newContent = "new";
            MessageUpdateRequestDto req = new MessageUpdateRequestDto(newContent);

            Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
            User author = new User("abc", "abc@a.com", "abc", null);
            Message message = new Message(oldContent, channel, author, List.of());
            message.setId(messageId);

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageMapper.toDto(any(Message.class))).thenAnswer(invocation -> {
                Message updated = invocation.getArgument(0);
                return new MessageDto(updated.getId(), null, null, updated.getContent(), null, null,
                    List.of());
            });

            // when
            MessageDto result = messageService.update(messageId, req);

            // then
            assertNotNull(result);
            assertEquals(newContent, result.content());
            verify(messageRepository).findById(messageId);
            verify(messageMapper).toDto(any(Message.class));
        }

        @Test
        @DisplayName("메세지 수정 실패")
        void message_update_fail() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageUpdateRequestDto req = new MessageUpdateRequestDto("new");
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(MessageNotFoundException.class, () -> messageService.update(messageId, req));
            verify(messageRepository).findById(messageId);

        }
    }

    @Nested
    public class message_delete_test {

        @Test
        @DisplayName("메세지 삭제 성공")
        void message_delete_success() {
            // given
            UUID messageId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
            User author = new User("abc", "abc@a.com", "abc", null);
            Message message = new Message("hello", channel, author, List.of());
            message.setId(messageId);
            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

            // when + then
            assertDoesNotThrow(() -> messageService.delete(messageId));
            verify(messageRepository).findById(messageId);
            verify(messageRepository).delete(message);

        }

        @Test
        @DisplayName("메세지 삭제 실패")
        void message_delete_fail() {
            // given
            UUID messageId = UUID.randomUUID();
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(MessageNotFoundException.class, () -> messageService.delete(messageId));
            verify(messageRepository).findById(messageId);

        }

    }

    @Nested
    public class message_findById_test {

        @Test
        @DisplayName("메세지 조회 성공")
        void message_findById_success() {
            // given
            UUID messageId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
            User author = new User("abc", "abc@a.com", "abc", null);
            Message message = new Message("hello", channel, author, List.of());
            message.setId(messageId);

            MessageDto expected = new MessageDto(messageId, null, null, "hello", null, null, List.of());

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageMapper.toDto(message)).thenReturn(expected);

            // when
            MessageDto result = messageService.find(messageId);

            // then
            assertEquals(expected, result);
            verify(messageRepository).findById(messageId);
            verify(messageMapper).toDto(message);

        }

        @Test
        @DisplayName("메세지 조회 실패")
        void message_findById_fail() {
            // given
            UUID messageId = UUID.randomUUID();
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(MessageNotFoundException.class, () -> messageService.find(messageId));
            verify(messageRepository).findById(messageId);

        }

    }


}
