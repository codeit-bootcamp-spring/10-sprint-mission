package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.Role;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class BasicChannelServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublic_success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지방", "공지방입니다.");
        ChannelDto dto = new ChannelDto(UUID.randomUUID(), IsPrivate.PUBLIC, "공지방", "공지방입니다.", null,
                Instant.now());

        when(channelMapper.toChannelDto(any(Channel.class))).thenReturn(dto);

        // when
        ChannelDto result = channelService.createPublic(request);

        // then
        assertNotNull(result);
        assertEquals(IsPrivate.PUBLIC, result.type());
        assertEquals("공지방", result.name());
        assertEquals("공지방입니다.", result.description());
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("공개 채널 생성 성공 - 설명 없음")
    void createPublic_success_withoutDescription() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지방", null);
        ChannelDto dto = new ChannelDto(UUID.randomUUID(), IsPrivate.PUBLIC, "공지방", null, null,
                Instant.now());

        when(channelMapper.toChannelDto(any(Channel.class))).thenReturn(dto);

        // when
        ChannelDto result = channelService.createPublic(request);

        // then
        assertNotNull(result);
        assertEquals(IsPrivate.PUBLIC, result.type());
        assertEquals("공지방", result.name());
        assertNull(result.description());
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivate_success() {
        // given
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
        User user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        ChannelDto dto = new ChannelDto(UUID.randomUUID(), IsPrivate.PRIVATE, null, null, null,
                Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(channelMapper.toChannelDto(any(Channel.class))).thenReturn(dto);

        // when
        ChannelDto result = channelService.createPrivate(request);

        // then
        assertNotNull(result);
        assertEquals(IsPrivate.PRIVATE, result.type());
        verify(channelRepository).save(any(Channel.class));
        verify(readStatusRepository).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - 존재하지 않는 사용자")
    void createPrivate_fail_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.createPrivate(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(channelRepository, never()).save(any());
        verify(readStatusRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 id로 채널 찾기 성공")
    void find_all_by_user_id_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("공지방", IsPrivate.PUBLIC, "공지방입니다.");
        ChannelDto dto = new ChannelDto(channelId, IsPrivate.PUBLIC, "공지방", "공지방입니다.", null,
                Instant.now());

        List<Channel> channels = List.of(channel);
        List<ReadStatus> readStatuses = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        when(channelRepository.findAllByUserId(userId)).thenReturn(channels);
        when(readStatusRepository.findAllByChannelIdIn(any())).thenReturn(readStatuses);
        when(messageRepository.findLastMessagesByChannelIds(any())).thenReturn(messages);
        when(channelMapper.toChannelDto(any(Channel.class), any(), any())).thenReturn(dto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("공지방", result.get(0).name());
    }

    @Test
    @DisplayName("사용자 id로 채널 찾기 - 채널 없음")
    void find_all_by_user_id_empty() {
        // given
        UUID userId = UUID.randomUUID();

        when(channelRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("채널 수정 성공")
    void update_success() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 공지방", "새 공지방입니다.");
        Channel channel = new Channel("공지방", IsPrivate.PUBLIC, "공지방입니다.");
        ChannelDto dto = new ChannelDto(channelId, IsPrivate.PUBLIC, "새 공지방", "새 공지방입니다.", null,
                Instant.now());

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(channelMapper.toChannelDto(any(Channel.class))).thenReturn(dto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertNotNull(result);
        assertEquals("새 공지방", result.name());
        assertEquals("새 공지방입니다.", result.description());
    }

    @Test
    @DisplayName("채널 수정 실패 - PRIVATE 채널 수정 시도")
    void update_fail_privateChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 공지방", "새 공지방입니다.");
        Channel channel = new Channel(null, IsPrivate.PRIVATE, null);

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelUpdateNotAllowedException.class);
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("공지방", IsPrivate.PUBLIC, "공지방입니다.");

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        // when
        channelService.delete(channelId);

        // then
        verify(messageRepository).deleteByChannelId(channelId);
        verify(readStatusRepository).deleteByChannelId(channelId);
        verify(channelRepository).deleteById(channelId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void delete_fail_channelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();

        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.delete(channelId))
                .isInstanceOf(ChannelNotFoundException.class);

        verify(messageRepository, never()).deleteByChannelId(any());
        verify(readStatusRepository, never()).deleteByChannelId(any());
        verify(channelRepository, never()).deleteById(any());
    }
}
