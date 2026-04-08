package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicChannelService basicChannelService;

    @Test
    @DisplayName("공개 채널을 생성할 수 있습니다.")
    void create_public_channel() {
        // given
        CreatePublicChannelRequestDTO request =
                new CreatePublicChannelRequestDTO("test", "description");

        Channel channel = new Channel("test", "description", ChannelType.PUBLIC);

        User user1 = new User("u1", "u1@test.com", "1234", null);
        User user2 = new User("u2", "u2@test.com", "1234", null);

        ChannelDto response =
                new ChannelDto(null, ChannelType.PUBLIC, "test", "description", List.of(), null);

        // 전체 유저 조회
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        // channel 저장
        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        // mapper 결과
        given(channelMapper.toDto(any(), any(), any())).willReturn(response);

        // when
        ChannelDto result = basicChannelService.createPublicChannel(request);

        // then
        assertThat(result).isEqualTo(response);
        // 핵심 검증
        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAll();

        // user 수만큼 readStatus 생성됨
        then(readStatusRepository).should(times(2)).save(any());

        then(channelMapper).should().toDto(any(), any(), any());
    }

    @Test
    @DisplayName("비공개 채널을 생성할 수 있습니다.")
    void create_private_channel() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);

        User user1 = new User("u1", "u1@test.com", "1234", null);
        User user2 = new User("u2", "u2@test.com", "1234", null);

        CreatePrivateChannelRequestDTO request =
                new CreatePrivateChannelRequestDTO(ids);

        // 서비스 실제 코드와 맞춤
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);

        ChannelDto response =
                new ChannelDto(null, ChannelType.PRIVATE, null, null, List.of(), null);

        // 참여 유저 조회
        given(userRepository.findAllById(ids)).willReturn(List.of(user1, user2));

        // 채널 저장
        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        // 내부 집계용 stub
        given(readStatusRepository.findAllByChannel_IdIn(any())).willReturn(List.of());
        given(messageRepository.findLastMessageAtByChannelIds(any())).willReturn(List.of());

        // mapper 결과
        given(channelMapper.toDto(any(), any(), any())).willReturn(response);

        // when
        ChannelDto result = basicChannelService.createPrivateChannel(request);

        // then
        assertThat(result).isEqualTo(response);

        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAllById(ids);
        then(readStatusRepository).should(times(2)).save(any(ReadStatus.class));
        then(channelMapper).should().toDto(any(), any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 유저가 있으면 비공개 채널 생성시 예외를 던집니다.")
    void if_user_not_found_fail_to_create_channel() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        User user1 = new User("u1", "u1@test.com", "1234", null);
        List<UUID> ids = List.of(id1, id2);

        CreatePrivateChannelRequestDTO request =
                new CreatePrivateChannelRequestDTO(ids);
        given(userRepository.findAllById(ids)).willReturn(List.of(user1));

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> basicChannelService.createPrivateChannel(request));

        then(readStatusRepository).should(never()).save(any());
        then(channelMapper).shouldHaveNoInteractions();
        then(userRepository).should().findAllById(ids);
    }

    @Test
    @DisplayName("공개 채널을 수정할 수 있습니다.")
    void update_public_channel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("test", "description", ChannelType.PUBLIC);
        UpdateChannelRequestDTO request
                = new UpdateChannelRequestDTO("update", "update description");
        ChannelDto response
                = new ChannelDto(channelId, ChannelType.PUBLIC, "update", "update description", List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelRepository.existsByName("update")).willReturn(false);

        // buildSingleChannelDto 내부 호출 대비
        given(readStatusRepository.findAllByChannel_IdIn(any())).willReturn(List.of());
        given(messageRepository.findLastMessageAtByChannelIds(any())).willReturn(List.of());
        given(channelMapper.toDto(any(), any(), any())).willReturn(response);

        // when
        ChannelDto result = basicChannelService.updateChannel(channelId, request);

        // then
        assertThat(result).isEqualTo(response);

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should().existsByName("update");
        then(channelMapper).should().toDto(any(), any(), any());

        // 실제로 값이 바뀌었는지 확인
        assertThat(channel.getName()).isEqualTo("update");
        assertThat(channel.getDescription()).isEqualTo("update description");
    }

    @Test
    @DisplayName("비공개 채널은 수정할 수 없습니다.")
    void can_not_update_private_channel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);
        UpdateChannelRequestDTO request
                = new UpdateChannelRequestDTO("update", "update description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when, then
        assertThrows(PrivateChannelUpdateException.class,
                () -> basicChannelService.updateChannel(channelId, request));
        // 값이 바뀌지 않아야 함
        assertThat(channel.getName()).isNull();
        assertThat(channel.getDescription()).isNull();

        // 채널 조회는 수행됨
        then(channelRepository).should().findById(channelId);

        // 이후 수정 관련 로직은 수행되면 안 됨
        then(channelRepository).should(never()).existsByName(any());
        then(channelMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("중복된 이름으로 수정하면 예외를 던집니다.")
    void if_new_name_already_exists_throw_exception() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("test", "description", ChannelType.PUBLIC);
        UpdateChannelRequestDTO request
                = new UpdateChannelRequestDTO("update", null);
        // channel에 id값 부여하기 위함
        ReflectionTestUtils.setField(channel, "id", channelId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelRepository.existsByName("update")).willReturn(true);

        // when, then
        assertThrows(DuplicateResourceException.class,
                () -> basicChannelService.updateChannel(channelId, request));

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should().existsByName("update");
        then(channelMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하는 채널은 삭제할 수 있습니다.")
    void delete_if_channel_exists() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel("test", "description", ChannelType.PUBLIC);
        // channel에 id값 부여하기 위함
        ReflectionTestUtils.setField(channel, "id", channelId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        basicChannelService.deleteChannel(channelId);

        // then
        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().deleteAllByChannel_Id(channelId);
        then(channelRepository).should().deleteById(channelId);
    }
}