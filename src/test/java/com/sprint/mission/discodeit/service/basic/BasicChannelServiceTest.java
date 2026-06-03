package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotUpdatableException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @InjectMocks
    private BasicChannelService basicChannelService;


    /*
        채널 생성
     */
    // [성공] 공개 채널 생성
    @Test
    @DisplayName("공개 채널 생성 완료")
    void create_public_channel_success(){
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "Meow World",
                "cat is my god"
        );

        // 가짜 객체 | 생성할 공개 채널 생성 및 저장
        ChannelEntity newChannel = new ChannelEntity(
                request.name(),
                request.description(),
                ChannelType.PUBLIC
        );
        given(channelMapper.toPublicEntity(request)).willReturn(newChannel);
        given(channelRepository.save(any(ChannelEntity.class))).willReturn(newChannel);

        // 가짜 응답 DTO 생성 및 저장
        ChannelDto expectedDto = ChannelDto.builder()
                .id(UUID.randomUUID())
                .type(newChannel.getType())
                .name(newChannel.getName())
                .description(newChannel.getDescription())
                .participants(null)
                .lastMessageAt(Instant.now())
                .build();
        given(channelMapper.toDto(any(ChannelEntity.class), any(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = basicChannelService.createPublicChannel(request);

        // then
        assertNotNull(result);
        assertEquals(request.name(), result.name());
        assertEquals(request.description(), result.description());
        assertEquals(ChannelType.PUBLIC, result.type());
    }

    // [성공] 비공개 채널 생성
    @Test
    @DisplayName("비공개 채널 생성 완료")
    void create_private_channel_success(){
        // given
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        // 비공개 채널 참여자 목록
        List<UUID> participantIds = List.of(firstUserId, secondUserId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // 가짜 객체 | 생성할 비공개 채널
        ChannelEntity newChannel = new ChannelEntity(null, null, ChannelType.PRIVATE);
        given(channelMapper.toPrivateEntity()).willReturn(newChannel);

        // 참여자 유효성 검증
        UserEntity firstMember = new UserEntity(
                "yushi",
                "yushi1@wish.com",
                "yushi1234");
        UserEntity secondMember = new UserEntity(
                "sakuya",
                "sakuya@wish.com",
                "sakuya1234");
        given(userRepository.findAllById(participantIds)).willReturn(List.of(firstMember, secondMember));

        // 비공개 채널 저장
        given(channelRepository.save(any(ChannelEntity.class))).willReturn(newChannel);

        // 가짜 응답 DTO 생성 및 저장
        ChannelDto expectedDto = ChannelDto.builder()
                .id(UUID.randomUUID())
                .type(newChannel.getType())
                .build();
        given(channelMapper.toDto(any(ChannelEntity.class), any(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = basicChannelService.createPrivateChannel(request);

        // then
        assertNotNull(result);
        assertEquals(2, newChannel.getReadStatuses().size());
    }

    // [실패] 비공개 채널 참여자 미존재
    @Test
    @DisplayName("비공개 채널 생성 살패: 참여하고자 하는 멤버가 존재하지 않을 경우, UserNotFoundException 발생")
    void create_private_channel_failure_participant_not_found(){
        // given
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        // 비공개 채널 참여자 목록
        List<UUID> participantIds = List.of(firstUserId, secondUserId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // 가짜 객체 | 생성할 비공개 채널
        ChannelEntity newChannel = new ChannelEntity(null, null, ChannelType.PRIVATE);
        given(channelMapper.toPrivateEntity()).willReturn(newChannel);

        // 참여자 유효성 검증
        given(userRepository.findAllById(participantIds)).willReturn(List.of());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> {
                    basicChannelService.createPrivateChannel(request);
                }
        );

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(channelRepository, never()).save(any(ChannelEntity.class));
    }

    /*
        공개 채널 수정
     */
    // [성공]
    @Test
    @DisplayName("공개 채널 수정 완료")
    void update_public_channel_success(){
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "Alien",
                "To be Alien together"
        );

        // 가짜 객체 | 기존 채널 정보
        ChannelEntity targetChannel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PUBLIC
        );

        // 유효성 검증
        given(channelRepository.findById(channelId)).willReturn(Optional.of(targetChannel));

        // when
        basicChannelService.update(channelId, request);

        // then
        assertEquals(request.newName(), targetChannel.getName());
        assertEquals(request.newDescription(), targetChannel.getDescription());
    }

    // [실패] 비공개 채널 수정 시도
    @Test
    @DisplayName("공개 채널 수정 실패: 비공개 채널 정보를 수정할 경우, AccessDeniedPrivateChannelException 발생")
    void update_private_channel_failure_access_denied(){
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "Alien",
                "To be Alien together"
        );

        // 가짜 객체 | 기존 채널 정보
        ChannelEntity targetChannel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PRIVATE
        );

        // 유효성 검증
        given(channelRepository.findById(channelId)).willReturn(Optional.of(targetChannel));

        // when
        PrivateChannelNotUpdatableException exception = assertThrows(
                PrivateChannelNotUpdatableException.class, () -> {
                    basicChannelService.update(channelId, request);
                }
        );

        // then
        assertEquals(ErrorCode.PRIVATE_CHANNEL_NOT_UPDATABLE, exception.getErrorCode());
        assertEquals("Meow World", targetChannel.getName());
        verify(channelRepository, never()).save(any(ChannelEntity.class));
    }

    /*
        채널 삭제
     */
    // [성공]
    @Test
    @DisplayName("채널 삭제 완료")
    void delete_channel_success(){
        // given
        UUID channelId = UUID.randomUUID();
        ChannelEntity targetChannel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PRIVATE
        );

        // 유효성 검증
        given(channelRepository.findById(channelId)).willReturn(Optional.of(targetChannel));

        // when
        basicChannelService.delete(channelId);

        // then
        verify(channelRepository, times(1)).delete(targetChannel);
    }

    // [실패] 해당 채널 미존재
    @Test
    @DisplayName("채널 삭제 실패: 해당 채널이 존재하지 않을 경우, ChannelNotFoundException 발생")
    void delete_channel_failure_not_found() {
        // given
        UUID channelId = UUID.randomUUID();

        // 유효성 검증
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when
        ChannelNotFoundException exception = assertThrows(
                ChannelNotFoundException.class, () -> {
                    basicChannelService.delete(channelId);
                }
        );

        // then
        assertEquals(ErrorCode.CHANNEL_NOT_FOUND, exception.getErrorCode());
        verify(channelRepository, never()).delete(any());
    }

    /*
        특정 사용자가 포함된 채널 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 사용자가 포함된 채널 목록 조회 완료")
    void find_all_channels_by_user_id_success(){
        // given
        UUID userId = UUID.randomUUID();

        // 가짜 객체 | 특정 사용자
        UserEntity user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", userId);

        // 유효성 검사
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // 가짜 객체 | 특정 사용자가 포함된 채널 목록
        ChannelEntity firstChannel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC);
        ChannelEntity secondChannel = new ChannelEntity(
                "Meow World",
                "cat is my god",
                ChannelType.PRIVATE);
        List<ChannelEntity> channels = List.of(firstChannel, secondChannel);

        // 특정 사용자가 포함된 채널 목록 조회
        given(channelRepository.findAllVisibleChannelByUserId(userId)).willReturn(channels);

        // 응답 DTO 생성
        ChannelDto channelDto = ChannelDto.builder().build();
        given(channelMapper.toDto(any(), any(), any())).willReturn(channelDto);

        // when
        List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(channelRepository, times(1)).findAllVisibleChannelByUserId(userId);
    }

    // [실패] 해당 사용자 미존재
    @Test
    @DisplayName("특정 사용자가 포함된 채널 목록 조회 실패: 특정 사용자가 존재하지 않을 경우, UserNotFoundException 발생")
    void find_all_channels_by_user_id_failure_not_found(){
        // given
        UUID userId = UUID.randomUUID();

        // 가짜 객체 | 특정 사용자
        UserEntity user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", userId);

        // 유효성 검사
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> {
                    basicChannelService.findAllByUserId(userId);
                }
        );

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(channelRepository, never()).findAllVisibleChannelByUserId(userId);
    }
}
