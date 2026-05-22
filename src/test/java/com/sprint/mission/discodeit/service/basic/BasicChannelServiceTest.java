package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelDuplicateNameException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotBeUpdatedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ChannelMapper channelMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicChannelService basicChannelService;

  @Test
  @DisplayName("사용자가 있을 때 PRIVATE 채널은 정상적으로 생성되어야 합니다.")
  void create_private_success() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    UUID channelId = UUID.randomUUID();
    ReflectionTestUtils.setField(channel, "id", channelId);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.save(any(Channel.class))).willReturn(channel);

    // when
    UUID result = basicChannelService.createPrivate(request);

    // then
    assertThat(result).isEqualTo(channelId);
    then(userRepository).should().findById(userId);
    then(channelRepository).should().save(any(Channel.class));
    then(readStatusRepository).should(times(1)).save(any());
  }

  @Test
  @DisplayName("존재하지 않는 사용자가 포함되면 PRIVATE 채널 생성 시 예외가 발생해야 합니다.")
  void create_private_fail_user_not_found() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> basicChannelService.createPrivate(request))
        .isInstanceOf(UserNotFoundException.class);

    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("PUBLIC 채널은 정상적으로 생성되어야 합니다.")
  void create_public_success() {
    // given
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("PUBLIC 채널 1", "PUBLIC 1 채널입니다.");

    UUID channelId = UUID.randomUUID();

    given(channelRepository.existsByName(request.name())).willReturn(false);
    given(channelRepository.createChannel(any(Channel.class))).willReturn(channelId);

    // when
    UUID result = basicChannelService.createPublic(request);

    // then
    assertThat(result).isEqualTo(channelId);
    then(channelRepository).should().existsByName(request.name());
    then(channelRepository).should().createChannel(any(Channel.class));
    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("중복된 PUBLIC 채널 이름으로 생성 시 예외가 발생해야 합니다.")
  void create_public_fail_duplicate_name() {
    // given
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("PUBLIC 채널 1", "PUBLIC 1 채널입니다.");

    given(channelRepository.existsByName(request.name())).willReturn(true);

    // when, then
    assertThatThrownBy(() -> basicChannelService.createPublic(request))
        .isInstanceOf(ChannelDuplicateNameException.class);

    then(channelRepository).should().existsByName(request.name());
    then(channelRepository).should(never()).createChannel(any(Channel.class));
  }

  @Test
  @DisplayName("사용자 ID로 참여 중인 채널 목록을 정상적으로 조회해야 합니다.")
  void findAllByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    Channel channel =
        new Channel(ChannelType.PUBLIC, "PUBLIC 채널 1", "PUBLIC 1채널 입니다.");
    ReflectionTestUtils.setField(channel, "id", channelId);

    ChannelResponse response = new ChannelResponse(
        channelId,
        channel.getChannelName(),
        channel.getDescription(),
        false,
        null,
        List.of()
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findAllChannel()).willReturn(List.of(channel));
    given(channelMapper.toResponse(any(Channel.class), any(), any())).willReturn(response);

    // when
    List<ChannelResponse> result = basicChannelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).channelId()).isEqualTo(channelId);
    assertThat(result.get(0).channelName()).isEqualTo("PUBLIC 채널 1");

    then(userRepository).should().findById(userId);
    then(channelRepository).should().findAllChannel();
    then(channelMapper).should().toResponse(any(Channel.class), any(), any());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 ID로 채널 목록 조회 시 예외가 발생해야 합니다.")
  void findAllByUserId_fail_not_found() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> basicChannelService.findAllByUserId(userId))
        .isInstanceOf(UserNotFoundException.class);

    then(channelRepository).should(never()).findAllChannel();
  }

  @Test
  @DisplayName("채널 정보는 정상적으로 수정되어야 합니다.")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel =
        new Channel(ChannelType.PUBLIC, "PUBLIC 채널 1", "PUBLIC 1 채널입니다.");
    ReflectionTestUtils.setField(channel, "id", channelId);

    ChannelUpdateRequest request =
        new ChannelUpdateRequest(channelId, "채널_수정", "채널 수정 테스트");

    ChannelResponse response = new ChannelResponse(
        channelId,
        "채널_수정",
        "채널 수정 테스트",
        false,
        null,
        List.of()
    );

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toResponse(any(Channel.class), any(), any())).willReturn(response);

    // when
    ChannelResponse result = basicChannelService.update(request);

    // then
    assertThat(result.channelId()).isEqualTo(channelId);
    assertThat(result.channelName()).isEqualTo("채널_수정");
    assertThat(result.description()).isEqualTo("채널 수정 테스트");

    then(channelRepository).should(times(2)).findById(channelId);
    then(channelRepository).should().saveChannel(channel);
    then(channelMapper).should().toResponse(any(Channel.class), any(), any());
  }

  @Test
  @DisplayName("PRIVATE 채널은 수정할 수 없습니다.")
  void update_fail_private_channel() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(channel, "id", channelId);

    ChannelUpdateRequest request =
        new ChannelUpdateRequest(channelId, "채널_수정", "채널 수정 테스트");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when, then
    assertThatThrownBy(() -> basicChannelService.update(request))
        .isInstanceOf(PrivateChannelCannotBeUpdatedException.class);

    then(channelRepository).should(never()).saveChannel(any(Channel.class));
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    // given
    UUID channelId = UUID.randomUUID();

    ChannelUpdateRequest request =
        new ChannelUpdateRequest(channelId, "채널_수정", "채널 수정 테스트");

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> basicChannelService.update(request))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should(never()).saveChannel(any(Channel.class));
  }

  @Test
  @DisplayName("채널은 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel =
        new Channel(ChannelType.PUBLIC, "PUBLIC 채널 1", "PUBLIC 1 채널입니다.");
    ReflectionTestUtils.setField(channel, "id", channelId);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when
    basicChannelService.delete(channelId);

    // then
    then(channelRepository).should().findById(channelId);
    then(channelRepository).should().delete(channel);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> basicChannelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should(never()).delete(any(Channel.class));
  }
}