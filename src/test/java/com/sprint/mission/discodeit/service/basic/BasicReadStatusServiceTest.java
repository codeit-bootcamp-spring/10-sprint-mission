package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private ReadStatusMapper readStatusMapper;

    @InjectMocks
    private BasicReadStatusService basicReadStatusService;

    @Test
    @DisplayName("유효한 요청으로 읽음 상태를 생성한다.")
    void create_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        CreateReadStatusRequestDto request =
            new CreateReadStatusRequestDto(userId, channelId, null);

        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        ReadStatus readStatus = new ReadStatus(user, channel);
        ReadStatusDto expectedDto =
            new ReadStatusDto(UUID.randomUUID(), channelId, userId, Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // when
        ReadStatusDto result = basicReadStatusService.create(request);

        // then
        assertEquals(expectedDto, result);
        verify(readStatusRepository).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("이미 읽음 상태가 존재하면 ReadStatusAlreadyExistsException이 발생한다.")
    void create_fail_duplicate() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        CreateReadStatusRequestDto request =
            new CreateReadStatusRequestDto(userId, channelId, null);

        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        org.springframework.test.util.ReflectionTestUtils.setField(channel, "id", channelId);

        ReadStatus existing = new ReadStatus(user, channel);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(existing));

        // when & then
        assertThrows(ReadStatusAlreadyExistsException.class,
            () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("존재하지 않는 유저로 읽음 상태 생성 시 UserNotFoundException이 발생한다.")
    void create_fail_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        CreateReadStatusRequestDto request =
            new CreateReadStatusRequestDto(userId, channelId, null);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
            () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("존재하지 않는 채널로 읽음 상태 생성 시 ChannelNotFoundException이 발생한다.")
    void create_fail_channel_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        CreateReadStatusRequestDto request =
            new CreateReadStatusRequestDto(userId, channelId, null);

        User user = new User("tester", "t@t.com", "pass");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class,
            () -> basicReadStatusService.create(request));
    }

    @Test
    @DisplayName("lastReadAt 지정 시 해당 시간으로 읽음 상태를 수정한다.")
    void update_with_specific_time_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        Instant specificTime = Instant.now().minusSeconds(60);
        UpdateReadStatusRequestDto request = new UpdateReadStatusRequestDto(specificTime);

        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        ReadStatus readStatus = new ReadStatus(user, channel);
        ReadStatusDto expectedDto =
            new ReadStatusDto(readStatusId, UUID.randomUUID(), UUID.randomUUID(), specificTime);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = basicReadStatusService.update(readStatusId, request);

        // then
        assertEquals(specificTime, readStatus.getLastReadAt());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("lastReadAt 미지정 시 현재 시간으로 읽음 상태를 수정한다.")
    void update_with_null_time_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UpdateReadStatusRequestDto request = new UpdateReadStatusRequestDto(null);

        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        ReadStatus readStatus = new ReadStatus(user, channel);
        ReadStatusDto expectedDto =
            new ReadStatusDto(readStatusId, UUID.randomUUID(), UUID.randomUUID(), Instant.now());

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        basicReadStatusService.update(readStatusId, request);

        // then
        assertNotNull(readStatus.getLastReadAt());
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 수정 시 ReadStatusNotFoundException이 발생한다.")
    void update_fail_not_found() {
        // given
        UUID fakeId = UUID.randomUUID();
        given(readStatusRepository.findById(fakeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ReadStatusNotFoundException.class,
            () -> basicReadStatusService.update(fakeId, new UpdateReadStatusRequestDto(null)));
    }

    @Test
    @DisplayName("유저 ID로 읽음 상태 목록을 조회한다.")
    void findAllByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        ReadStatus readStatus = new ReadStatus(user, channel);

        given(userRepository.existsById(userId)).willReturn(true);
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
        given(readStatusMapper.toDto(readStatus))
            .willReturn(new ReadStatusDto(UUID.randomUUID(), UUID.randomUUID(), userId, Instant.now()));

        // when
        List<ReadStatusDto> results = basicReadStatusService.findAllByUserId(userId);

        // then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("읽음 상태를 삭제한다.")
    void delete_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        User user = new User("tester", "t@t.com", "pass");
        Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
        ReadStatus readStatus = new ReadStatus(user, channel);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));

        // when
        basicReadStatusService.delete(readStatusId);

        // then
        verify(readStatusRepository).delete(readStatus);
    }
}