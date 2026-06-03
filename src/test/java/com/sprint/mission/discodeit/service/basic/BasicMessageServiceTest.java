package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

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
    private PageResponseMapper pageResponseMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @InjectMocks
    private BasicMessageService basicMessageService;

    /*
        메시지 생성
     */
    // [성공]
    @Test
    @DisplayName("메시지 생성 완료")
    void create_message_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                userId,
                channelId
        );

        // 가짜 객체 | 작성자 및 채널
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity();

        // 유효성 검증
        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // 가짜 객체 | 생성할 메시지
        MessageEntity newMessage = new MessageEntity(
                request.content(),
                author,
                channel
        );
        List<MultipartFile> attachments = new ArrayList<>();
        given(messageMapper.toEntity(request, author, channel)).willReturn(newMessage);
        given(messageRepository.save(newMessage)).willReturn(newMessage);

        // 응답 DTO 생성 및 저장
        UserDto userDto = UserDto.builder().build();
        MessageDto expectedDto = MessageDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .content(newMessage.getContent())
                .channelId(channelId)
                .author(userDto)
                .build();
        given(messageMapper.toDto(newMessage)).willReturn(expectedDto);

        // when
        MessageDto result = basicMessageService.create(request, attachments);

        // then
        assertEquals(request.content(), result.content());
        assertNotNull(result.author());
        assertEquals(channelId, result.channelId());
    }

    // [실패] 작성자 미존재
    @Test
    @DisplayName("메시지 생성 실패: 작성자가 존재하지 않을 경우, UserNotFoundException 발생")
    void create_message_failure_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                userId,
                channelId
        );

        // 유효성 검증
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> {
                    basicMessageService.create(request, null);
                }
        );

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    // [실패] 채널 미존재
    @Test
    @DisplayName("메시지 생성 실패: 채널이 존재하지 않을 경우, ChannelNotFoundException 발생")
    void create_message_failure_channel_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                userId,
                channelId
        );

        // 가짜 객체 | 작성자
        UserEntity author = new UserEntity();

        // 유효성 검증
        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when
        ChannelNotFoundException exception = assertThrows(
                ChannelNotFoundException.class, () -> {
                    basicMessageService.create(request, null);
                }
        );

        // then
        assertEquals(ErrorCode.CHANNEL_NOT_FOUND, exception.getErrorCode());
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    // [실패] 첨부 파일 생성 실패
    @Test
    @DisplayName("메시지 생성 실패: 첨부 파일 생성에 실패한 경우, BinaryContentFileProcessingErrorException 발생")
    void create_message_failure_binary_content_file_processing() throws IOException {
        // given
        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                userId,
                clientId
        );

        // 가짜 객체 | 작성자 및 채널
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity();

        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(channelRepository.findById(clientId)).willReturn(Optional.of(channel));

        // 가짜 객체 | 기존 메시지 정보
        MessageEntity newMessage = new MessageEntity(
                request.content(),
                author,
                channel
        );
        ReflectionTestUtils.setField(newMessage, "id", UUID.randomUUID());

        given(messageMapper.toEntity(any(), any(), any())).willReturn(newMessage);

        // 가짜 객체 | 생성할 첨부 파일
        MultipartFile mockFile = mock(MultipartFile.class);
        List<MultipartFile> attachments = List.of(mockFile);

        BinaryContentEntity attempt = new BinaryContentEntity(
                "test.png",
                100L,
                "image/png"
        );
        given(binaryContentRepository.save(any(BinaryContentEntity.class))).willReturn(attempt);

        given(mockFile.getName()).willReturn("attachments");
        given(mockFile.getOriginalFilename()).willReturn("test.png");
        given(mockFile.getBytes()).willThrow(new IOException("Disk Error!"));

        // when
        BinaryContentFileProcessingErrorException exception = assertThrows(
                BinaryContentFileProcessingErrorException.class, () -> {
                    basicMessageService.create(request, attachments);
                }
        );

        // then
        assertEquals(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR, exception.getErrorCode());
        assertEquals("attachments", exception.getDetails().get("filename"));
        verify(messageRepository, never()).save(any());
    }

    /*
        메시지 수정
     */
    // [성공]
    @Test
    @DisplayName("메시지 수정 완료")
    void update_message_success() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest(
                "KILL ME KILL ME"
        );

        // 가짜 객체 | 기존 메시지 정보
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity();
        MessageEntity targetMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );

        // 유효성 검증
        given(messageRepository.findWithDetails(messageId)).willReturn(Optional.of(targetMessage));

        // 응답 DTO 생성 및 저장
        UserDto userDto = UserDto.builder()
                .build();
        MessageDto expectedDto = MessageDto.builder()
                .id(messageId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .content(request.newContent())
                .channelId(channel.getId())
                .author(userDto)
                .build();
       given(messageMapper.toDto(targetMessage)).willReturn(expectedDto);

        // when
        basicMessageService.update(messageId, request);

        // then
        assertEquals(request.newContent(), targetMessage.getContent());
    }

    /*
        메시지 삭제
     */
    // [성공]
    @Test
    @DisplayName("메시지 삭제 완료")
    void delete_message_success() {
        // given
        UUID messageId = UUID.randomUUID();

        // 가짜 객체 | 삭제할 메시지
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity();
        MessageEntity targetMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );

        // 유효성 검증
        given(messageRepository.findWithDetails(messageId)).willReturn(Optional.of(targetMessage));

        // when
        basicMessageService.delete(messageId);

        // then
        verify(messageRepository, times(1)).delete(targetMessage);
    }

    // [실패] 해당 메시지 미존재
    @Test
    @DisplayName("메시지 삭제 실패: 해당 메시지가 존재하지 않는 경우, MessageNotFoundException 발생")
    void delete_message_failure_message_not_found() {
        // given
        UUID messageId = UUID.randomUUID();

        // 유효성 검증
        given(messageRepository.findWithDetails(messageId)).willReturn(Optional.empty());

        // when
        MessageNotFoundException exception = assertThrows(
                MessageNotFoundException.class, () -> {
                    basicMessageService.delete(messageId);
                }
        );

        // then
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, exception.getErrorCode());
        verify(channelRepository, never()).delete(any());
    }

    /*
        특정 채널 메시지 목록 조회
     */
    // [성공] 첫 페이지
    @Test
    @DisplayName("특정 채널에서 발행된 메시지 목록 조회 성공")
    void find_all_messages_by_channel_id_success_first_page() {
        // given
        UUID channelId = UUID.randomUUID();

        // 가짜 객체 | 작성자 및 특정 채널
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PUBLIC
        );
        ReflectionTestUtils.setField(channel, "id", channelId);

        // 유효성 검사
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // 가짜 객체 | 페이지
        Instant cursor = null;
        int size = 10;
        Pageable limit = PageRequest.of(0, size + 1);

        // 가짜 객체 | 특정 채널에서 발행된 메시지 목록
        MessageEntity firstMessage = new MessageEntity(
                "Hey, Badu",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "너도 함께 돼보자고 Alien이",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);

        // 특정 채널에서 발행된 메시지 목록 조회
        given(messageRepository.findFirstPageByChannelId(channelId, limit)).willReturn(messages);
        given(messageRepository.countByChannelId(channelId)).willReturn(2L);

        // 응답 DTO 생성
        MessageDto messageDto = MessageDto.builder().build();
        given(messageMapper.toDto(any())).willReturn(messageDto);

        // 페이지 응답 DTO 생성
        PageResponse<MessageDto> pageResponse = PageResponse.<MessageDto>builder()
                .content(List.of(messageDto, messageDto))
                .nextCursor(null)
                .size(2)
                .hasNext(true)
                .totalElements(2L)
                .build();
        given(pageResponseMapper.fromCursor(
                ArgumentMatchers.<List<MessageDto>>any(),
                nullable(String.class),
                anyInt(),
                anyBoolean(),
                any()
        )).willReturn(pageResponse);

        // when
        PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, cursor, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(messageRepository, times(1)).findFirstPageByChannelId(channelId, limit);
    }

    // [성공] 두 번쨰 페이지 이후
    @Test
    @DisplayName("특정 채널에서 발행된 메시지 목록 조회 성공")
    void find_all_messages_by_channel_id_success_second_page() {
        // given
        UUID channelId = UUID.randomUUID();

        // 가짜 객체 | 작성자 및 특정 채널
        UserEntity author = new UserEntity();
        ChannelEntity channel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PUBLIC
        );
        ReflectionTestUtils.setField(channel, "id", channelId);

        // 유효성 검사
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // 가짜 객체 | 페이지
        Instant cursor = Instant.now();
        int size = 10;
        Pageable limit = PageRequest.of(0, size + 1);

        // 가짜 객체 | 특정 채널에서 발행된 메시지 목록
        MessageEntity firstMessage = new MessageEntity(
                "Hey, Badu",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "너도 함께 돼보자고 Alien이",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);

        // 특정 채널에서 발행된 메시지 목록 조회
        given(messageRepository.findNextPageByChannelId(channelId, cursor, limit)).willReturn(messages);
        given(messageRepository.countByChannelId(channelId)).willReturn(2L);

        // 응답 DTO 생성
        MessageDto messageDto = MessageDto.builder().build();
        given(messageMapper.toDto(any())).willReturn(messageDto);

        // 페이지 응답 DTO 생성
        PageResponse<MessageDto> pageResponse = PageResponse.<MessageDto>builder()
                .content(List.of(messageDto, messageDto))
                .nextCursor(null)
                .size(2)
                .hasNext(true)
                .totalElements(2L)
                .build();
        given(pageResponseMapper.fromCursor(
                ArgumentMatchers.<List<MessageDto>>any(),
                nullable(String.class),
                anyInt(),
                anyBoolean(),
                any()
        )).willReturn(pageResponse);

        // when
        PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, cursor, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(messageRepository, times(1)).findNextPageByChannelId(channelId, cursor, limit);
    }


    // [실패] 해당 채널 미존재
    @Test
    @DisplayName("특정 채널에서 발행된 메시지 목록 조회 실패: 해당 채널이 존재하지 않을 경우, ChannelNotFoundException 발생")
    void find_all_messages_by_channel_id_failure_not_found() {
        // given
        UUID channelId = UUID.randomUUID();

        // 가짜 객체 | 특정 채널
        ChannelEntity channel = new ChannelEntity();
        ReflectionTestUtils.setField(channel, "id", channelId);

        // 유효성 검사
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // 가짜 객체 | 페이지
        Instant cursor = Instant.now();
        int size = 10;
        Pageable limit = PageRequest.of(0, size + 1);

        // when
        ChannelNotFoundException exception = assertThrows(
                ChannelNotFoundException.class, () -> {
                    basicMessageService.findAllByChannelId(channelId, cursor, size);
                }
        );

        // then
        assertEquals(ErrorCode.CHANNEL_NOT_FOUND, exception.getErrorCode());
        verify(messageRepository, never()).findFirstPageByChannelId(channelId, limit);
        verify(messageRepository, never()).findNextPageByChannelId(channelId, cursor, limit);
    }
}
