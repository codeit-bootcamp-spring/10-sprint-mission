package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowed;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @InjectMocks
    private BasicChannelService basicChannelService;

    @Mock private ChannelRepository channelRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private ChannelMapper channelMapper;
    @Mock private UserMapper userMapper;

    @Test
    @DisplayName("public 채널 생성 성공 - 정상적인 요청 시 channelDTO 반환")
    void public_채널_생성_성공_테스트() {
        // given
        Channel channel = new Channel("테스트", "테스트 채널입니다.");
        ChannelCreateRequestPublic createRequestPublic = new ChannelCreateRequestPublic("테스트", "테스트 채널입니다.");

        given(channelRepository.existsByNameAndType(anyString(), any(ChannelType.class))).willReturn(false);
        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        // when
        ChannelDTO result = basicChannelService.createPublic(createRequestPublic);

        // then
        assertEquals("테스트", result.name());
        assertEquals(ChannelType.PUBLIC, result.type());
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("public 채널 생성 실패 - 중복 이름 - ChannelAlreadyExistsException 반환")
    void public_채널_생성_실패_테스트() {
        // given
        ChannelCreateRequestPublic requestPublic = new ChannelCreateRequestPublic("테스트", "테스트 채널입니다.");

        given(channelRepository.existsByNameAndType(anyString(), any(ChannelType.class))).willReturn(true);

        // when, then
        assertThrows(ChannelAlreadyExistsException.class, () -> basicChannelService.createPublic(requestPublic));

        then(channelRepository).should().existsByNameAndType(anyString(), any(ChannelType.class));
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("private 채널 생성 성공 - 정상적인 요청 시 ChannelDTO 반환")
    void private_채널_생성_성공_테스트() {
        // given
        Channel channel = Channel.createPrivateChannel();
        ChannelCreateRequestPrivate requestPrivate = new ChannelCreateRequestPrivate(List.of(UUID.randomUUID(), UUID.randomUUID()));
        User user = new User("test", "", "12345678", null);
        ReadStatus readStatus = new ReadStatus(user, channel);
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), null, false);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
        given(userMapper.toDTO(any(User.class))).willReturn(userDTO);

        // when
        ChannelDTO result = basicChannelService.createPrivate(requestPrivate);

        // then
        assertEquals(ChannelType.PRIVATE, result.type());
        assertEquals(2, result.participants().size());
    }

    @Test
    @DisplayName("private 채널 생성 실패 - 없는 유저 - UserNotFoundException 반환")
    void private_채널_생성_실패_테스트(){
        // given
        Channel channel = Channel.createPrivateChannel();
        ChannelCreateRequestPrivate requestPrivate = new ChannelCreateRequestPrivate(List.of(UUID.randomUUID(), UUID.randomUUID()));

        given(userRepository.findById(any(UUID.class))).willThrow(new UserNotFoundException(UUID.randomUUID()));

        // when, then
        assertThrows(UserNotFoundException.class, () -> basicChannelService.createPrivate(requestPrivate));
    }

    // Todo: claude code 사용
    @Test
    @DisplayName("채널 목록 조회 성공 - 특정 유저가 참여한 모든 채널 반환")
    void 유저가_참여한_채널_목록_조회_성공_테스트() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        // mock Channel 설정
        Channel mockChannel = mock(Channel.class);
        given(mockChannel.getId()).willReturn(channelId);
        given(mockChannel.getType()).willReturn(ChannelType.PUBLIC);

        // mock ReadStatus 설정
        ReadStatus mockReadStatus = mock(ReadStatus.class);
        given(mockReadStatus.getChannel()).willReturn(mockChannel);
        given(mockReadStatus.getUser()).willReturn(mock(User.class));

        // mock Message 설정
        Message mockMessage = mock(Message.class);
        given(mockMessage.getChannel()).willReturn(mockChannel);
        given(mockMessage.getCreatedAt()).willReturn(Instant.now());

        given(channelRepository.findVisibleChannelsByUserId(any(UUID.class)))
                .willReturn(List.of(mockChannel));
        given(userRepository.findAllByChannelIdIn(anyList()))
                .willReturn(List.of(mockReadStatus));
        given(messageRepository.findAllByChannelIdIn(anyList()))
                .willReturn(List.of(mockMessage));
        given(userMapper.toDTO(any(User.class)))
                .willReturn(new UserDTO(UUID.randomUUID(), "test", "test@naver.com", null, false));

        // when
        List<ChannelDTO> results = basicChannelService.findAllByUserID(userId);

        // then
        assertEquals(1, results.size());
        assertEquals(channelId, results.get(0).id());
        then(channelRepository).should().findVisibleChannelsByUserId(any(UUID.class));
        then(userRepository).should().findAllByChannelIdIn(anyList());
        then(messageRepository).should().findAllByChannelIdIn(anyList());
    }

    @Test
    @DisplayName("채널 수정 성공 - 정상적인 요청 시 수정된 ChannelDTO 반환")
    void 채널_수정_성공_테스트() {
        // given
        Channel channel = new Channel("테스트 채널", "수정하기 전입니다.");
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest updateRequest = new ChannelUpdateRequest("수정한 테스트 채널", "수정한 후입니다.");

        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));
        given(userRepository.findAllByChannelId(any(UUID.class))).willReturn(List.of(new User("test", "test@test.com", "12345678", null)));
        given(userMapper.toDTO(any(User.class))).willReturn(new UserDTO(UUID.randomUUID(), "test", "test@test.com", null, false));
        given(messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(any(UUID.class))).willReturn(Instant.now());

        // when
        ChannelDTO result = basicChannelService.update(channelId, updateRequest);

        // then
        assertEquals(result.name(), updateRequest.newName());
        assertEquals(result.description(), updateRequest.newDescription());
    }

    @Test
    @DisplayName("채널 수정 실패 - Private 채널 수정 시도 - PrivateChannelUpdateNotAllowedException")
    void 채널_수정_실패_테스트() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest updateRequest = new ChannelUpdateRequest("수정한 테스트 채널", "수정한 후입니다.");
        Channel channel = Channel.createPrivateChannel();

        // given을 사용할 때는 mock 객체여야 한다 !
        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));

        // when, then
        assertThrows(PrivateChannelUpdateNotAllowed.class, () -> basicChannelService.update(channelId, updateRequest));
        then(channelRepository).should().findById(any(UUID.class)); // 호출 O
        // 호출 X
        then(userRepository).should(never()).findAllByChannelId(any(UUID.class));
        then(messageRepository).should(never()).findFirstByChannelIdOrderByCreatedAtDesc(any(UUID.class));
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("채널 삭제 성공 - 정상적인 요청 시 채널 삭제")
    void 채널_삭제_성공_테스트() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = mock(Channel.class);

        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));
        // when
        basicChannelService.deleteChannel(channelId);

        // then
        then(channelRepository).should().delete(any());
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널 삭제 시도 시 ChannelNotFoundException 반환")
    void 채널_삭제_실패_테스트() {
        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.findById(any(UUID.class))).willThrow(new ChannelNotFoundException(channelId));

        // when, then
        assertThrows(ChannelNotFoundException.class, () -> basicChannelService.deleteChannel(channelId));
        then(channelRepository).should().findById(any(UUID.class));
        then(messageRepository).should(never()).deleteAllByChannelId(any(UUID.class));
        then(readStatusRepository).should(never()).deleteAllByChannelId(any(UUID.class));
    }
}