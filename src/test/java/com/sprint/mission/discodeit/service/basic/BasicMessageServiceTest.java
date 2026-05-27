package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Mock private MessageRepository messageRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private BinaryContentStorage binaryContentStorage;
    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private PageResponseMapper pageResponseMapper;
    @Mock private BinaryContentRepository binaryContentRepository;

    @Test
    @DisplayName("메시지 생성 성공 - 정상적인 요청 시 MessageDTO 반환")
    void 메시지_생성_성공_테스트() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Channel mockChannel = mock(Channel.class);
        User mockUser = mock(User.class);
        Message mockMessage = mock(Message.class);  // 👈 추가

        MessageCreateRequest createRequest = new MessageCreateRequest("테스트 메시지입니다", channelId, userId);

        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));
        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(mockChannel));
        given(messageRepository.save(any(Message.class))).willReturn(mockMessage);  // 👈 추가
        given(messageMapper.toDTO(any(Message.class))).willReturn(  // 👈 추가
                new MessageDTO(UUID.randomUUID(), Instant.now(), Instant.now(),
                        "테스트 메시지입니다", channelId, null, List.of()));

        // when
        MessageDTO result = basicMessageService.create(createRequest, List.of());

        // then
        assertEquals(createRequest.content(), result.content());
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널 시 ChannelNotFoundException 반환")
    void 메시지_생성_실패_테스트() {
        // given
        UUID channelId = UUID.randomUUID();
        User mockUser = mock(User.class);
        MessageCreateRequest request = new MessageCreateRequest("테스트 메시지입니다.", channelId, UUID.randomUUID());

        given(channelRepository.findById(any(UUID.class))).willThrow(new ChannelNotFoundException(channelId));
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));

        // when, then
        assertThrows(ChannelNotFoundException.class, () -> basicMessageService.create(request, List.of()));
        then(userRepository).should().findById(any(UUID.class));
    }

    // Todo: 여기 다시 체크
    @Test
    @DisplayName("메시지 목록 조회 성공 - 특정 채널의 모든 메시지 반환")
    void 채널별_메시지_목록_조회_성공_테스트() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Pageable pageable = Pageable.unpaged();
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), "test", "", null, false);
        MessageDTO messageDTO = new MessageDTO(
                UUID.randomUUID(),
                createdAt,
                createdAt,
                "테스트 메시지",
                channelId,
                userDTO,
                List.of()
        );

        Message mockMessage = mock(Message.class);
        Slice<Message> mockSlice = new SliceImpl<>(List.of(mockMessage));

        given(messageRepository.findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class), any(Pageable.class)))
                .willReturn(mockSlice);
        given(messageMapper.toDTO(any(Message.class))).willReturn(messageDTO);
        given(pageResponseMapper.fromSlice(any(), any())).willReturn(
                new PageResponse<>(
                        List.of(messageDTO),
                        createdAt,
                        10,
                        false,
                        null
                  ));

        // when
        PageResponse<MessageDTO> result = basicMessageService.findMessagesByChannel(channelId, createdAt, pageable);

        // then
        assertEquals(messageDTO, result.content().get(0));
        then(messageRepository).should().findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class), any(Pageable.class));
        then(messageMapper).should().toDTO(any(Message.class));
        then(pageResponseMapper).should().fromSlice(any(), any());
    }

    // 수정
    @Test
    @DisplayName("메시지 수정 성공 - 정상적인 요청 시 수정된 MessageDTO 반환")
    void 메시지_수정_성공_테스트() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정할 메시지입니다.");
        Message mockMessage = mock(Message.class);
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), "test", "", null, false);
        given(messageRepository.findById(any(UUID.class))).willReturn(Optional.of(mockMessage));
        given(messageMapper.toDTO(any(Message.class))).willReturn(
                new MessageDTO(
                        messageId,
                        Instant.now(),
                        Instant.now(),
                        request.newContent(),
                        UUID.randomUUID(),
                        userDTO,
                        List.of()
                )
        );
        // when
        MessageDTO result = basicMessageService.update(messageId, request);

        // then
        assertEquals(request.newContent(), result.content());

        then(messageRepository).should().findById(any(UUID.class));
        then(mockMessage).should().updateContents(request.newContent());
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지 수정 시도 시 MessageNotFoundException 반환")
    void 메시지_수정_실패_테스트() {
        // given
        UUID messageId = UUID.randomUUID();
        Message mockMessage = mock(Message.class);

        given(messageRepository.findById(any(UUID.class))).willThrow(new MessageNotFoundException(messageId));

        // when, then
        assertThrows(MessageNotFoundException.class, () -> basicMessageService.update(messageId, new MessageUpdateRequest("")));
        then(messageRepository).should().findById(any(UUID.class));
        then(mockMessage).should(never()).updateContents(anyString());
    }

    // 삭제
    @Test
    @DisplayName("메시지 삭제 성공 - 정상적인 요청 시 메시지 삭제")
    void 메시지_삭제_성공_테스트() {
        // given
        UUID messageId = UUID.randomUUID();
        Message mockMessage = mock(Message.class);

        given(messageRepository.findById(any(UUID.class))).willReturn(Optional.of(mockMessage));

        // when
        basicMessageService.deleteMessage(messageId);

        // then
        then(messageRepository).should().delete(any(Message.class));

    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지 삭제 시도 시 MessageNotFoundException 반환")
    void 메시지_삭제_실패_테스트() {
        // given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(any(UUID.class))).willThrow(new MessageNotFoundException(messageId));

        // when, then
        assertThrows(MessageNotFoundException.class, () -> basicMessageService.deleteMessage(messageId));
        then(messageRepository).should(never()).delete(any(Message.class));
    }
}