package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusService readStatusService;

    @Mock
    private MessageService messageService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublicChannel_success() {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "전체 공지");
        ChannelDto expected = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "공지",
                "전체 공지",
                List.of(),
                null
        );

        given(channelMapper.toDto(any(Channel.class), eq(List.of()))).willReturn(expected);

        ChannelDto result = channelService.createPublicChannel(request);

        assertThat(result).isEqualTo(expected);
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패")
    void createPublicChannel_fail_whenSaveThrows() {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "전체 공지");
        willThrow(new IllegalStateException("save failed")).given(channelRepository).save(any(Channel.class));

        assertThatThrownBy(() -> channelService.createPublicChannel(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("save failed");
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivateChannel_success() {
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Set.of(userId));
        ChannelDto expected = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PRIVATE,
                null,
                null,
                List.of(),
                null
        );

        given(channelMapper.toDto(any(Channel.class), eq(List.of()))).willReturn(expected);

        ChannelDto result = channelService.createPrivateChannel(request);

        assertThat(result).isEqualTo(expected);
        then(channelRepository).should().save(any(Channel.class));
        then(readStatusService).should().createInitialReadStatuses(any(), eq(Set.of(userId)), any(Instant.class));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패")
    void createPrivateChannel_fail_whenCreateInitialReadStatusesThrows() {
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Set.of(userId));
        willThrow(new IllegalArgumentException("read status failed"))
                .given(readStatusService)
                .createInitialReadStatuses(any(), eq(Set.of(userId)), any(Instant.class));

        assertThatThrownBy(() -> channelService.createPrivateChannel(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("read status failed");
    }

    @Test
    @DisplayName("PUBLIC 채널 정보 수정 성공")
    void updateChannelInfo_success() {
        UUID channelId = UUID.randomUUID();
        Channel channel = Channel.buildPublic("old", "desc");
        setId(channel, channelId);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "updated");
        ChannelDto expected = new ChannelDto(channelId, ChannelType.PUBLIC, "new", "updated", List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(java.util.Optional.of(channel));
        given(channelMapper.toDto(channel, List.of())).willReturn(expected);

        ChannelDto result = channelService.updateChannelInfo(channelId, request);

        assertThat(result).isEqualTo(expected);
        assertThat(channel.getName()).isEqualTo("new");
        assertThat(channel.getDescription()).isEqualTo("updated");
    }

    @Test
    @DisplayName("PRIVATE 채널 정보 수정 시 예외 발생")
    void updateChannelInfo_fail_whenChannelIsPrivate() {
        UUID channelId = UUID.randomUUID();
        Channel channel = Channel.buildPrivate();
        setId(channel, channelId);
        given(channelRepository.findById(channelId)).willReturn(java.util.Optional.of(channel));

        assertThatThrownBy(() -> channelService.updateChannelInfo(
                channelId,
                new PublicChannelUpdateRequest("new", "updated")
        )).isInstanceOf(PrivateChannelUpdateException.class);
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_success() {
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID readStatusId = UUID.randomUUID();

        Channel channel = Channel.buildPublic("general", "desc");
        setId(channel, channelId);
        User user = new User("dustle", "1234", "dustle@test.com");
        Message message = new Message(user, channel, "hello");
        setId(message, messageId);
        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
        setId(readStatus, readStatusId);

        given(channelRepository.findById(channelId)).willReturn(java.util.Optional.of(channel));

        channelService.deleteChannel(channelId);

        then(messageService).should().deleteMessage(messageId);
        then(readStatusService).should().deleteReadStatus(readStatusId);
        then(channelRepository).should().delete(channel);
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 실패")
    void deleteChannel_fail_whenChannelNotFound() {
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> channelService.deleteChannel(channelId))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("유저별 Visible 채널 조회")
    void findAllChannelsByUserId_success() {
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        Channel channel = Channel.buildPublic("general", "desc");
        setId(channel, channelId);
        User participant = new User("dustle", "1234", "dustle@test.com");
        setId(participant, UUID.randomUUID());
        ReadStatus readStatus = new ReadStatus(participant, channel, Instant.now());
        UserDto participantDto = new UserDto((UUID) ReflectionTestUtils.getField(participant, "id"), "dustle", "dustle@test.com", null, null);
        ChannelDto expected = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "desc", List.of(participantDto), null);

        given(channelRepository.findVisibleChannels(requesterId, ChannelType.PUBLIC, ChannelType.PRIVATE))
                .willReturn(List.of(channel));
        given(userMapper.toDto(participant)).willReturn(participantDto);
        given(channelMapper.toDto(channel, List.of(participantDto))).willReturn(expected);

        List<ChannelDto> result = channelService.findAllChannelsByUserId(requesterId);

        assertThat(result).containsExactly(expected);
    }

    @Test
    @DisplayName("유저별 Visible 채널 조회 실패")
    void findAllChannelsByUserId_fail_whenRepositoryThrows() {
        UUID requesterId = UUID.randomUUID();
        willThrow(new RuntimeException("lookup failed"))
                .given(channelRepository)
                .findVisibleChannels(requesterId, ChannelType.PUBLIC, ChannelType.PRIVATE);

        assertThatThrownBy(() -> channelService.findAllChannelsByUserId(requesterId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("lookup failed");
    }

    private static void setId(Object target, UUID id) {
        ReflectionTestUtils.setField(target, "id", id);
    }
}
