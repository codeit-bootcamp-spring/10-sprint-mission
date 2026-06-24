package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotBeUpdatedException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelParticipantRequiredException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private BasicChannelService basicChannelService;

    private String name;
    private String description;

    @BeforeEach
    void setUp() {
        name = "testChannel";
        description = "test channel 입니다.";
    }

    @Nested
    @DisplayName("공개 채널 생성 테스트")
    class createPublicChannel {

        @Test
        @DisplayName("공개 채널을 생성할 수 있다.")
        void success_create_public_channel() {
            // given(준비)
            PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);

            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);
            ChannelDto expectedChannelDto = new ChannelDto(channelId, ChannelType.PUBLIC, name, description, List.of(), null);

            given(channelMapper.toDto(any(Channel.class))).willReturn(expectedChannelDto);

            // when(실행)
            ChannelDto result = basicChannelService.createPublicChannel(request);

            // then(검증)
            assertEquals(expectedChannelDto, result);
            assertEquals(expectedChannelDto.type(), result.type());
            assertEquals(expectedChannelDto.name(), result.name());
            assertEquals(expectedChannelDto.description(), result.description());

            verify(channelRepository).save(any(Channel.class));
            verify(channelMapper).toDto(any(Channel.class));
        }
    }

    @Nested
    @DisplayName("비공개 채널 생성 테스트")
    class createPrivateChannel {
        private User user1;
        private User user2;

        @BeforeEach
        void setUpPrivateChannel() {
            user1 = new User("test1@gmail.com", "test1", "1234", null);
            user2 = new User("test2@gmail.com", "test2", "1234", null);
        }

        @Test
        @DisplayName("비공개 채널을 생성할 수 있다.")
        void success_create_private_channel() {
            // given(준비)
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            ReflectionTestUtils.setField(user1, "id", userId1);
            ReflectionTestUtils.setField(user2, "id", userId2);
            UserDto userDto1 = new UserDto(userId1, user1.getUsername(), user1.getEmail(), null, false, user1.getRole());
            UserDto userDto2 = new UserDto(userId2, user2.getUsername(), user2.getEmail(), null, false, user2.getRole());

            List<UUID> participantIds = List.of(userId1, userId2);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PRIVATE, null, null);
            ReflectionTestUtils.setField(channel, "id", channelId);
            ChannelDto expectedChannelDto = new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(userDto1, userDto2), null);

            given(userRepository.findById(userId1)).willReturn(Optional.of(user1));
            given(userRepository.findById(userId2)).willReturn(Optional.of(user2));
            given(channelMapper.toDto(any(Channel.class))).willReturn(expectedChannelDto);

            // when(실행)
            ChannelDto result = basicChannelService.createPrivateChannel(request);

            // then(검증)
            assertEquals(expectedChannelDto, result);
            assertEquals(expectedChannelDto.type(), result.type());

            verify(userRepository, times(2)).findById(any());
            verify(channelRepository).save(any(Channel.class));
            verify(readStatusRepository, times(2)).save(any(ReadStatus.class));
            verify(channelMapper).toDto(any(Channel.class));
        }

        @Test
        @DisplayName("비공개 채널에 참가자가 없으면 예외가 발생한다.")
        void fail_create_private_channel_when_no_participants() {
            // given(준비)
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());

            // when(실행), then(검증)
            assertThrows(PrivateChannelParticipantRequiredException.class,
                    () -> basicChannelService.createPrivateChannel(request));

            verify(userRepository, never()).findById(any());
            verify(channelRepository, never()).save(any(Channel.class));
            verify(readStatusRepository, never()).save(any(ReadStatus.class));
            verify(channelMapper, never()).toDto(any(Channel.class));
        }

        @Test
        @DisplayName("참가자의 ID가 null이면 예외가 발생한다.")
        void fail_create_private_channel_when_participantId_null() {
            // given(준비)
            List<UUID> participantIds = new ArrayList<>(Arrays.asList(null, null));
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicChannelService.createPrivateChannel(request));

            verify(userRepository, never()).findById(any());
            verify(channelRepository, never()).save(any(Channel.class));
            verify(readStatusRepository, never()).save(any(ReadStatus.class));
            verify(channelMapper, never()).toDto(any(Channel.class));
        }

        @Test
        @DisplayName("해당 ID를 가진 참가자를 찾을 수 없으면 예외가 발생한다.")
        void fail_create_private_channel_when_participant_not_found() {
            // given(준비)
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            ReflectionTestUtils.setField(user1, "id", userId1);
            ReflectionTestUtils.setField(user2, "id", userId2);

            List<UUID> participantIds = List.of(userId1, userId2);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            given(userRepository.findById(any())).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicChannelService.createPrivateChannel(request));

            verify(userRepository).findById(any());
            verify(channelRepository, never()).save(any(Channel.class));
            verify(readStatusRepository, never()).save(any(ReadStatus.class));
            verify(channelMapper, never()).toDto(any(Channel.class));
        }
    }


    @Nested
    @DisplayName("채널 단건 조회 테스트")
    class findChannel {

        @Test
        @DisplayName("채널 ID로 채널 단건 조회를 할 수 있다.")
        void success_find_channel() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);
            ChannelDto expectedChannelDto = new ChannelDto(channelId, channel.getType(), channel.getName(), channel.getDescription(), List.of(), null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(channelMapper.toDto(channel)).willReturn(expectedChannelDto);

            // when(실행)
            ChannelDto result = basicChannelService.find(channelId);

            // then(검증)
            assertEquals(expectedChannelDto, result);
            assertEquals(expectedChannelDto.type(), result.type());
            assertEquals(expectedChannelDto.name(), result.name());
            assertEquals(expectedChannelDto.description(), result.description());

            verify(channelRepository).findById(channelId);
            verify(channelMapper).toDto(channel);
        }

        @Test
        @DisplayName("채널 ID가 null이면 예외가 발생한다.")
        void fail_find_channel_when_channelId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicChannelService.find(null));

            verify(channelRepository, never()).findById(any());
            verify(channelMapper, never()).toDto(any(Channel.class));
        }

        @Test
        @DisplayName("해당 ID를 가진 채널을 찾을 수 없다면 예외가 발생한다.")
        void fail_find_channel_when_channel_not_found() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicChannelService.find(channelId));

            verify(channelRepository).findById(channelId);
            verify(channelMapper, never()).toDto(any(Channel.class));
        }
    }

    @Nested
    @DisplayName("사용자가 볼 수 있는 채널 목록 조회 테스트")
    class findChannelList {

        @Test
        @DisplayName("특정 사용자가 채널 목록을 조회할 수 있다.")
        void success_find_channel_list() {
            // given(준비)
            UUID userId = UUID.randomUUID();

            User user = new User("test@gmail.com", "test", "1234", null);
            ReflectionTestUtils.setField(user, "id", userId);

            UserDto userDto = new UserDto(userId, user.getUsername(), user.getEmail(), null, true, user.getRole());

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            UUID channelId1 = UUID.randomUUID();
            UUID channelId2 = UUID.randomUUID();
            UUID channelId3 = UUID.randomUUID();
            UUID channelId4 = UUID.randomUUID();

            Channel channel1 = new Channel(ChannelType.PUBLIC, "testChannel1", "test channel1입니다.");
            Channel channel2 = new Channel(ChannelType.PUBLIC, "testChannel2", "test channel2입니다.");
            Channel channel3 = new Channel(ChannelType.PRIVATE, null, null);
            Channel channel4 = new Channel(ChannelType.PRIVATE, null, null);

            ReflectionTestUtils.setField(channel1, "id", channelId1);
            ReflectionTestUtils.setField(channel2, "id", channelId2);
            ReflectionTestUtils.setField(channel3, "id", channelId3);
            ReflectionTestUtils.setField(channel4, "id", channelId4);

            List<Channel> channelList = List.of(channel1, channel2, channel3, channel4);
            List<UUID> channelIds = List.of(channelId1, channelId2, channelId3, channelId4);
            List<UUID> privateChannelIds = List.of(channelId3, channelId4);

            given(channelRepository.findChannelByUserId(ChannelType.PUBLIC, userId)).willReturn(channelList);

            List<ChannelLastMessageAtDto> channelLastMessageAtDtoList = List.of(
                    new ChannelLastMessageAtDto(channelId1, Instant.now()),
                    new ChannelLastMessageAtDto(channelId2, Instant.now()),
                    new ChannelLastMessageAtDto(channelId3, Instant.now()),
                    new ChannelLastMessageAtDto(channelId4, Instant.now())
            );

            given(messageRepository.findLastMessageAtDtoByChannelIds(channelIds)).willReturn(channelLastMessageAtDtoList);

            UUID readStatusId3 = UUID.randomUUID();
            UUID readStatusId4 = UUID.randomUUID();

            ReadStatus readStatus3 = new ReadStatus(user, channel3, null, true);
            ReadStatus readStatus4 = new ReadStatus(user, channel4, Instant.now(), true);

            ReflectionTestUtils.setField(readStatus3, "id", readStatusId3);
            ReflectionTestUtils.setField(readStatus4, "id", readStatusId4);
            List<ReadStatus> readStatusList = List.of(readStatus3, readStatus4);

            given(readStatusRepository.findAllByChannelIdsWithUserAndChannel(privateChannelIds)).willReturn(readStatusList);

            ChannelDto channelDto1 = new ChannelDto(channelId1, channel1.getType(), channel1.getName(), channel1.getDescription(), List.of(), Instant.now());
            ChannelDto channelDto2 = new ChannelDto(channelId2, channel2.getType(), channel2.getName(), channel2.getDescription(), List.of(), null);
            ChannelDto channelDto3 = new ChannelDto(channelId3, channel3.getType(), channel3.getName(), channel3.getDescription(), List.of(userDto), Instant.now());
            ChannelDto channelDto4 = new ChannelDto(channelId4, channel4.getType(), channel4.getName(), channel4.getDescription(), List.of(userDto), null);

            given(channelMapper.toListDto(eq(channel1), anyMap(), anyMap())).willReturn(channelDto1);
            given(channelMapper.toListDto(eq(channel2), anyMap(), anyMap())).willReturn(channelDto2);
            given(channelMapper.toListDto(eq(channel3), anyMap(), anyMap())).willReturn(channelDto3);
            given(channelMapper.toListDto(eq(channel4), anyMap(), anyMap())).willReturn(channelDto4);
            List<ChannelDto> expectedChannelDtoList = List.of(channelDto1, channelDto2, channelDto3, channelDto4);

            // when(실행)
            List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

            // then(검증)
            assertEquals(expectedChannelDtoList, result);
            assertEquals(4, result.size());

            assertEquals(expectedChannelDtoList.get(0), result.get(0));
            assertEquals(expectedChannelDtoList.get(1), result.get(1));
            assertEquals(expectedChannelDtoList.get(2), result.get(2));
            assertEquals(expectedChannelDtoList.get(3), result.get(3));

            verify(userRepository).findById(userId);
            verify(channelRepository).findChannelByUserId(ChannelType.PUBLIC, userId);
            verify(messageRepository).findLastMessageAtDtoByChannelIds(channelIds);
            verify(readStatusRepository).findAllByChannelIdsWithUserAndChannel(privateChannelIds);
            verify(userMapper, times(2)).toDto(any(User.class));
            verify(channelMapper, times(4)).toListDto(any(Channel.class), anyMap(), anyMap());
        }

        @Test
        @DisplayName("특정 사용자에게 볼 수 있는 채널이 없을 때 빈 채널 목록을 출력할 수 있다.")
        void success_find_empty_channel_list() {
            // given(준비)
            UUID userId = UUID.randomUUID();

            User user = new User("test@gmail.com", "test", "1234", null);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(channelRepository.findChannelByUserId(ChannelType.PUBLIC, userId)).willReturn(List.of());
            given(messageRepository.findLastMessageAtDtoByChannelIds(List.of())).willReturn(List.of());
            given(readStatusRepository.findAllByChannelIdsWithUserAndChannel(List.of())).willReturn(List.of());

            List<ChannelDto> expectedChannelDtoList = List.of();

            // when(실행)
            List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

            // then(검증)
            assertEquals(expectedChannelDtoList, result);
            assertEquals(0, result.size());

            verify(userRepository).findById(userId);
            verify(channelRepository).findChannelByUserId(ChannelType.PUBLIC, userId);
            verify(messageRepository).findLastMessageAtDtoByChannelIds(anyList());
            verify(readStatusRepository).findAllByChannelIdsWithUserAndChannel(anyList());
            verify(userMapper, never()).toDto(any(User.class));
            verify(channelMapper, never()).toListDto(any(Channel.class), anyMap(), anyMap());
        }

        @Test
        @DisplayName("사용자 ID가 null이면 예외가 발생한다.")
        void fail_channel_list_when_userId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicChannelService.findAllByUserId(null));

            verify(userRepository, never()).findById(any());
            verify(channelRepository, never()).findChannelByUserId(eq(ChannelType.PUBLIC), any());
            verify(messageRepository, never()).findLastMessageAtDtoByChannelIds(any());
            verify(readStatusRepository, never()).findAllByChannelIdsWithUserAndChannel(any());
            verify(channelMapper, never()).toListDto(any(Channel.class), anyMap(), anyMap());
        }

        @Test
        @DisplayName("해당 ID로 사용자를 찾을 수 없으면 예외가 발생한다.")
        void fail_channel_list_when_user_not_found() {
            // given(준비)
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicChannelService.findAllByUserId(userId));

            verify(userRepository).findById(userId);
            verify(channelRepository, never()).findChannelByUserId(eq(ChannelType.PUBLIC), any());
            verify(messageRepository, never()).findLastMessageAtDtoByChannelIds(any());
            verify(readStatusRepository, never()).findAllByChannelIdsWithUserAndChannel(any());
            verify(channelMapper, never()).toListDto(any(Channel.class), anyMap(), anyMap());
        }
    }

    @Nested
    @DisplayName("공개 채널 정보 수정 테스트")
    class updatePublicChannel {

        @Test
        @DisplayName("해당 채널 ID로 공개 채널 정보를 수정할 수 있다.")
        void success_update_public_channel() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateName", "updateDescription");
            ChannelDto expectedChannelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "updateName", "updateDescription", List.of(), null);

            given(channelMapper.toDto(channel)).willReturn(expectedChannelDto);

            // when(실행)
            ChannelDto result = basicChannelService.update(channelId, request);

            // then(검증)
            assertEquals(expectedChannelDto, result);
            assertEquals(expectedChannelDto.type(), result.type());
            assertEquals(expectedChannelDto.name(), result.name());
            assertEquals(expectedChannelDto.description(), result.description());

            verify(channelRepository).findById(channelId);
            verify(channelMapper).toDto(channel);
        }

        @Test
        @DisplayName("채널 ID가 null일 경우 예외가 발생한다.")
        void fail_update_public_channel_when_channelId_null() {
            // given(준비)
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateName", "updateDescription");

            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicChannelService.update(null, request));

            verify(channelRepository, never()).findById(any());
            verify(channelMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("해당 ID를 가진 사용자를 찾을 수 없다면 예외가 발생한다.")
        void fail_update_public_channel_when_channel_not_found() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateName", "updateDescription");

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicChannelService.update(channelId, request));

            verify(channelRepository).findById(any());
            verify(channelMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("비공개 채널에 수정 시도할 경우 예외가 발생한다.")
        void fail_update_public_channel_when_channelType_private() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PRIVATE, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updateName", "updateDescription");

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when(실행), then(검증)
            assertThrows(PrivateChannelCannotBeUpdatedException.class,
                    () -> basicChannelService.update(channelId, request));

            verify(channelRepository).findById(channelId);
            verify(channelMapper, never()).toDto(channel);

        }

        @Test
        @DisplayName("변경사항이 없을 경우 예외 로직을 던짐 ")
        void fail_update_public_channel_when_not_change() {
            // given(준비)
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(name, description);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when(실행), then(검증)
            assertThrows(NoChangeValueException.class,
                    () -> basicChannelService.update(channelId, request));

            verify(channelRepository).findById(channelId);
            verify(channelMapper, never()).toDto(channel);
        }
    }


    @Nested
    @DisplayName("채널 삭제 테스트")
    class deleteChannel {
        UUID channelId;

        @BeforeEach
        void setUpDeleteChannel() {
            channelId = UUID.randomUUID();
        }

        @Test
        @DisplayName("채널을 삭제할 수 있다.")
        void success_delete_channel() {
            // given(준비)
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            ReflectionTestUtils.setField(channel, "id", channelId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when(실행)
            basicChannelService.delete(channelId);

            // then(검증)
            verify(channelRepository).findById(channelId);
            verify(channelRepository).delete(channel);
        }

        @Test
        @DisplayName("채널 ID가 null이면 예외가 발생한다.")
        void fail_delete_channel_when_channelId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicChannelService.delete(null));

            verify(channelRepository, never()).findById(any());
            verify(channelRepository, never()).deleteById(any());
            verify(channelRepository, never()).delete(any(Channel.class));
        }

        @Test
        @DisplayName("해당 ID를 가진 채널이 없으면 예외가 발생한다.")
        void fail_delete_channel_when_channel_not_found() {
            // given(준비)
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ChannelNotFoundException.class,
                    () -> basicChannelService.delete(channelId));

            verify(channelRepository).findById(channelId);
            verify(channelRepository, never()).delete(any(Channel.class));
        }
    }
}