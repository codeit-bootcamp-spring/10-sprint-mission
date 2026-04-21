package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    ChannelRepository channelRepository;
    @Mock
    MessageMapper messageMapper;
    @Mock
    BinaryContentStorage binaryContentStorage;
    @Mock
    BinaryContentRepository binaryContentRepository;
    @InjectMocks
    BasicMessageService messageService;

    // create
    @Test
    @DisplayName("메시지 생성 시 성공하여 messageDto 반환해야 한다")
    void create_message_success() {
        // given
        String content = "안녕하세요";
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest();
        request.setAuthorId(authorId);
        request.setChannelId(channelId);
        request.setContent(content);

        User user = new User("김현재", "fred@naver.com", "1234");
        Channel channel = new Channel("채널", "설명");

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "attachment1", "dummy.png", "image/png", mockByte
        );
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        MessageDto expectDto = new MessageDto(
                UUID.randomUUID(), null, channelId, Instant.now(), Instant.now(), new ArrayList<>(), content
        );
        BinaryContent binaryContent = new BinaryContent();
        UUID binaryContentId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", binaryContentId);

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(binaryContentId,mockByte)).willReturn(binaryContentId);
        given(messageRepository.save(any(Message.class))).willReturn(new Message(user, content, channel));
        given(messageMapper.toDto(any(Message.class))).willReturn(expectDto);

        // when
        MessageDto resultDto = messageService.create(request, multipartFiles);

        // then
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getContent()).isEqualTo(content);

        then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
        then(binaryContentStorage).should(times(1)).put(any(), any());
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 시 유저id를 찾지못해 user not found 예외를 발생히야한다")
    void create_message_user_not_found_exception_fail() {
        // given
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest();
        request.setAuthorId(authorId);

        when(userRepository.findById(request.getAuthorId())).thenReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> messageService.create(request,new ArrayList<>()))
                .isInstanceOf(UserNotFoundException.class);
        verify(channelRepository, never()).findById(any(UUID.class));
        then(channelRepository).should(never()).findById(any(UUID.class));
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(binaryContentStorage).should(never()).put(any(),any());
        then(messageRepository).should(never()).save(any(Message.class));
        then(messageMapper).should(never()).toDto(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 시 채널id를 찾지 못해 channel not found 예외를 발생히야 한다")
    void create_message_channel_not_found_exception_fail() {
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest();
        request.setAuthorId(authorId);
        request.setChannelId(channelId);

        given(userRepository.findById(request.getAuthorId())).willReturn(Optional.of(new User()));
        given(channelRepository.findById(request.getChannelId())).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> messageService.create(request,new ArrayList<>()))
                .isInstanceOf(ChannelNotFoundException.class);
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(binaryContentStorage).should(never()).put(any(),any());
        then(messageRepository).should(never()).save(any(Message.class));
        then(messageMapper).should(never()).toDto(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 시 파일 업로드에 실패하여 file upload failure 예외를 발생히야한다")
    void create_message_file_upload_exception_fail() {
        // given
        String content = "안녕하세요";
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest();
        request.setAuthorId(authorId);
        request.setChannelId(channelId);
        request.setContent(content);

        User user = new User("김현재", "fred@naver.com", "1234");
        Channel channel = new Channel("채널", "설명");

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "attachment1", "dummy.png", "image/png", mockByte
        );
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        BinaryContent binaryContent = new BinaryContent();
        UUID binaryContentId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", binaryContentId);

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(binaryContentId,mockByte)).willThrow(new FileUploadFailException());

        // when
        // then
        assertThatThrownBy(() -> messageService.create(request, multipartFiles))
                .isInstanceOf(FileUploadFailException.class);

        verify(messageRepository, never()).save(any(Message.class));
        then(messageRepository).should(never()).save(any(Message.class));
        then(messageMapper).should(never()).toDto(any(Message.class));
    }

    // update
    @Test
    @DisplayName("메시지 수정 시 성공하여 MessageDto 반환해야한다")
    void update_message_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = new Message(
                new User(),
                "안녕하세요",
                new Channel());
        ReflectionTestUtils.setField(message,"id",messageId);

        String content = "안녕!";
        MessageUpdateRequest request = new MessageUpdateRequest(content);

        MessageDto expectDto = new MessageDto(
                messageId,
                null,
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                new ArrayList<>(),
                request.getNewContent()
        );

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(expectDto);
        // when
        MessageDto resultDto = messageService.update(messageId,request);
        // then
        assertThat(resultDto.getContent()).isEqualTo(expectDto.getContent());
    }

    @Test
    @DisplayName("메시지 수정 시 messageid를 찾지 못해서 message not found 예외 발생히야한다")
    void update_message_not_found_exception_fail() {
        // given
        UUID messageId = UUID.randomUUID();

        String content = "안녕!";
        MessageUpdateRequest request = new MessageUpdateRequest(content);

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> messageService.update(messageId, request))
                .isInstanceOf(MessageNotFoundException.class);
        then(messageMapper).should(never()).toDto(any(Message.class));
    }

    // delete
    @Test
    @DisplayName("메시지 삭제 시 성공 해야한다")
    void delete_message_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = new Message();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        // when
        messageService.delete(messageId);
        // then
        then(messageRepository).should(times(1)).delete(any(Message.class));
    }

    @Test
    @DisplayName("메시지 삭제 시 messageid를 찾지 못해서 message not found 예외 발생히야한다")
    void delete_message_not_found_exception_fail() {
        // given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> messageService.delete(messageId))
                .isInstanceOf(MessageNotFoundException.class);
        then(messageRepository).should(never()).delete(any(Message.class));
    }
}