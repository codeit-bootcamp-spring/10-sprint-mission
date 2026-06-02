package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.Response;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelParticipantException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService channelService;

    private UUID publicChannelId;
    private UUID privateChannelId;
    private Channel publicChannel;
    private Channel privateChannel;
    private ChannelDto.Response publicChannelResponse;
    private ChannelDto.Response privateChannelResponse;
    private ChannelDto.PublicChannelCreateRequest publicChannelCreateRequest;
    private ChannelDto.PrivateChannelCreateRequest privateChannelCreateRequest;

    @BeforeEach
    void setUp() {
        publicChannelId = UUID.randomUUID();
        privateChannelId = UUID.randomUUID();

        publicChannel = new Channel(ChannelType.PUBLIC, "General", "Desc");
        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);

        privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);

        publicChannelResponse = new ChannelDto.Response(
                publicChannelId,
                ChannelType.PUBLIC,
                "General",
                "Desc",
                List.of(),
                Instant.now());

        publicChannelCreateRequest = new PublicChannelCreateRequest("General", "Desc");
    }

    @Nested
    @DisplayName("채널 생성 (create)")
    class Create {

        @Test
        @DisplayName("성공: 공개 채널 생성")
        void createPublic_Success() {
            // given
            given(channelRepository.save(any())).willReturn(publicChannel);
            given(channelMapper.toResponse(any(), anyList())).willReturn(publicChannelResponse);

            // when
            ChannelDto.Response result = channelService.create(publicChannelCreateRequest);

            // then
            assertThat(result).isEqualTo(publicChannelResponse);
            verify(channelRepository).save(any());
        }

        @Test
        @DisplayName("성공: 비공개 채널 생성 (참여자 ID 중복 시에도 Set으로 처리되어 정상 생성)")
        void createPrivate_Success_WithDuplicateIds() {
            // given
            UUID user1Id = UUID.randomUUID();
            UUID user2Id = UUID.randomUUID();
            User user1 = mock(User.class);
            User user2 = mock(User.class);
            given(user1.getId()).willReturn(user1Id);
            given(user2.getId()).willReturn(user2Id);

            UserDto.Response user1Response = new Response(user1Id, "user1", "", null, true, Role.USER);
            UserDto.Response user2Response = new Response(user2Id, "user2", "", null, true, Role.USER);

            given(userMapper.toResponse(user1)).willReturn(user1Response);
            given(userMapper.toResponse(user2)).willReturn(user2Response);

            privateChannelResponse = new ChannelDto.Response(
                    privateChannelId,
                    ChannelType.PRIVATE,
                    null,
                    null,
                    List.of(userMapper.toResponse(user1), userMapper.toResponse(user2)),
                    Instant.now());

            given(channelMapper.toResponse(any(), anyList())).willReturn(privateChannelResponse);
            given(userRepository.findAllById(any())).willReturn(List.of(user1, user2));
            given(channelRepository.save(any())).willReturn(privateChannel);

            ChannelDto.PrivateChannelCreateRequest request = new ChannelDto.PrivateChannelCreateRequest(
                    Set.of(user1Id, user2Id));

            // when
            ChannelDto.Response result = channelService.create(request);

            // then
            assertThat(result).isEqualTo(privateChannelResponse);
            verify(readStatusRepository, times(2)).save(any());
            verify(channelRepository).save(any());
        }

        @Test
        @DisplayName("실패: 비공개 채널 생성 - 존재하지 않는 유저 포함")
        void createPrivate_Fail_UserNotFound() {
            // given
            UUID user1Id = UUID.randomUUID();
            UUID user2Id = UUID.randomUUID();
            User user1 = mock(User.class);
            given(user1.getId()).willReturn(user1Id);
            given(userRepository.findAllById(any())).willReturn(List.of(user1));

            privateChannelCreateRequest = new PrivateChannelCreateRequest(Set.of(user1Id, user2Id));

            // when & then
            assertThatThrownBy(() -> channelService.create(privateChannelCreateRequest))
                    .isInstanceOf(UserNotFoundException.class);
            verify(channelRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 비공개 채널 생성 - 참여자 2명 미만")
        void createPrivate_Fail_MinParticipants() {
            // given
            UUID user1Id = UUID.randomUUID();
            User user1 = mock(User.class);
            given(user1.getId()).willReturn(user1Id);
            given(userRepository.findAllById(any())).willReturn(List.of(user1));

            ChannelDto.PrivateChannelCreateRequest request = new ChannelDto.PrivateChannelCreateRequest(
                    Set.of(user1Id));

            // when & then
            assertThatThrownBy(() -> channelService.create(request))
                    .isInstanceOf(PrivateChannelParticipantException.class);
            verify(channelRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("채널 조회 (find/findAll/findAllByUserId)")
    class Read {

        @Test
        @DisplayName("단일 조회 성공")
        void find_Success() {
            // given
            given(channelRepository.findById(publicChannelId)).willReturn(
                    Optional.of(publicChannel));
            given(channelMapper.toResponse(any(), anyList())).willReturn(publicChannelResponse);

            // when
            ChannelDto.Response response = channelService.find(publicChannelId);

            // then
            verify(channelRepository).findById(publicChannelId);
            assertThat(response).isEqualTo(publicChannelResponse);
        }

        @Test
        @DisplayName("성공: 전체 채널 조회 시 비공개 채널의 참여자 정보도 함께 로드됨")
        void findAll_MultipleChannels_Success() {
            // given
            // 1. 레포지토리 결과 설정 (public -> private 순서)
            given(channelRepository.findAll()).willReturn(List.of(publicChannel, privateChannel));

            User user1 = mock(User.class);
            ReadStatus rs = mock(ReadStatus.class);
            given(rs.getChannel()).willReturn(privateChannel);
            given(rs.getUser()).willReturn(user1);

            // 2. 참여자 정보 조회 결과 설정
            given(readStatusRepository.findAllByChannelIdsWithUser(
                    List.of(publicChannelId, privateChannelId)))
                    .willReturn(List.of(rs));

            // 3. 매퍼 모킹 (검증을 위해 가짜 응답 반환 설정)
            UserDto.Response mappedUser1 = mock(UserDto.Response.class);
            given(userMapper.toResponse(user1)).willReturn(mappedUser1);
            given(channelMapper.toResponse(any(), anyList())).willReturn(
                    mock(ChannelDto.Response.class));

            // when
            channelService.findAll();

            // then
            // ArgumentCaptor 생성
            ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
            ArgumentCaptor<List<Response>> participantsCaptor = ArgumentCaptor.forClass(
                    List.class);

            // channelMapper.toResponse가 2번 호출되었는지 확인하고 인자 캡처
            verify(channelMapper, times(2)).toResponse(channelCaptor.capture(),
                    participantsCaptor.capture());

            List<Channel> capturedChannels = channelCaptor.getAllValues();
            List<List<UserDto.Response>> capturedParticipants = participantsCaptor.getAllValues();

            // 1. 실제 찾은 채널이 전달된 채널과 동일한지 순서대로 검증
            assertThat(capturedChannels).containsExactly(publicChannel, privateChannel);

            // 2. 첫 번째 채널(Public)은 참여자 목록이 비어있어야 함
            assertThat(capturedParticipants.get(0)).isEmpty();

            // 3. 두 번째 채널(Private)은 user1이 매핑된 정보가 포함되어 있어야 함
            assertThat(capturedParticipants.get(1))
                    .hasSize(1)
                    .containsExactly(mappedUser1);

            // 추가: user1에 대해 매퍼가 호출되었는지도 확인
            verify(userMapper).toResponse(user1);
        }

        @Test
        @DisplayName("성공: 유저의 채널 목록 조회")
        void findAllByUserId_Success() {
            // given
            UUID userId = UUID.randomUUID();
            given(userRepository.existsById(userId)).willReturn(true);
            given(channelRepository.findAllAccessibleByUserId(userId))
                    .willReturn(List.of(publicChannel, privateChannel));

            User participant = mock(User.class);
            ReadStatus rs = mock(ReadStatus.class);
            given(rs.getChannel()).willReturn(privateChannel);
            given(rs.getUser()).willReturn(participant);

            given(readStatusRepository.findAllByChannelIdsWithUser(
                    List.of(publicChannelId, privateChannelId)))
                    .willReturn(List.of(rs));

            UserDto.Response participantResponse = mock(UserDto.Response.class);
            given(userMapper.toResponse(participant)).willReturn(participantResponse);
            given(channelMapper.toResponse(any(), anyList())).willReturn(
                    mock(ChannelDto.Response.class));
            // when
            channelService.findAllByUserId(userId);

            // then
            // ArgumentCaptor로 전달 인자 캡처
            ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
            ArgumentCaptor<List<UserDto.Response>> participantsCaptor = ArgumentCaptor.forClass(
                    List.class);

            // channelMapper.toResponse가 2번 호출됨을 검증하며 캡처
            verify(channelMapper, times(2)).toResponse(channelCaptor.capture(),
                    participantsCaptor.capture());

            // 검증 1: 채널 리포지토리에서 찾은 채널들이 순서대로 매퍼에 전달되었는지 확인
            assertThat(channelCaptor.getAllValues()).containsExactly(publicChannel, privateChannel);

            // 검증 2: 퍼블릭 채널(첫 번째 호출)은 참여자 목록이 비어있어야 함
            assertThat(participantsCaptor.getAllValues().get(0)).isEmpty();

            // 검증 3: 프라이빗 채널(두 번째 호출)은 캡처된 참여자 정보가 포함되어야 함
            assertThat(participantsCaptor.getAllValues().get(1))
                    .hasSize(1)
                    .containsExactly(participantResponse);
        }

            @Test
            @DisplayName("실패: 존재하지 않는 유저의 채널 목록 조회 시도")
            void findAllByUserId_Fail_UserNotFound () {
                // given
                UUID userId = UUID.randomUUID();
                given(userRepository.existsById(userId)).willReturn(false);

                // when & then
                assertThatThrownBy(() -> channelService.findAllByUserId(userId))
                        .isInstanceOf(UserNotFoundException.class);
            }

            @Test
            @DisplayName("단일 조회 실패: 존재하지 않는 ID")
            void find_Fail_NotFound () {
                // given
                given(channelRepository.findById(publicChannelId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> channelService.find(publicChannelId))
                        .isInstanceOf(ChannelNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("채널 수정 (update)")
        class Update {

            @Test
            @DisplayName("성공: 공개 채널 정보 수정")
            void update_Success() {
                // given
                ChannelDto.UpdatePublicRequest request = new ChannelDto.UpdatePublicRequest(
                        "NewName",
                        "NewDesc");
                given(channelRepository.findById(publicChannelId)).willReturn(
                        Optional.of(publicChannel));
                given(channelMapper.toResponse(any(), anyList())).willReturn(publicChannelResponse);

                // when
                channelService.update(publicChannelId, request);

                // then
                assertThat(publicChannel.getName()).isEqualTo("NewName");
            }

            @Test
            @DisplayName("실패: 비공개 채널 수정 시도 시 예외 발생")
            void update_Fail_PrivateChannel() {
                // given
                given(channelRepository.findById(any())).willReturn(Optional.of(privateChannel));
                ChannelDto.UpdatePublicRequest request = new ChannelDto.UpdatePublicRequest(
                        "NewName",
                        "NewDesc");

                // when & then
                assertThatThrownBy(() -> channelService.update(UUID.randomUUID(), request))
                        .isInstanceOf(PrivateChannelUpdateNotAllowedException.class);
            }
        }

        @Nested
        @DisplayName("채널 삭제 (delete)")
        class Delete {

            @Test
            @DisplayName("성공: 채널 삭제")
            void delete_Success() {
                // given
                given(channelRepository.findById(publicChannelId)).willReturn(
                        Optional.of(publicChannel));

                // when
                channelService.delete(publicChannelId);

                // then
                verify(channelRepository).delete(publicChannel);
            }

            @Test
            @DisplayName("실패: 삭제하려는 채널이 존재하지 않음")
            void delete_Fail_NotFound() {
                // given
                given(channelRepository.findById(publicChannelId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> channelService.delete(publicChannelId))
                        .isInstanceOf(ChannelNotFoundException.class);
            }
        }
    }
