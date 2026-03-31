package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;


import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelDuplicateNameException;

import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotBeUpdatedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

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

  @Mock
  private ChannelService channelService;

  @InjectMocks
  private BasicChannelService basicChannelService;

  @Test
  @DisplayName("사용자가 있을 때 PRIVATE 채널은 정상적으로 생성되어야 합니다.")
  void create_private_success() {
    //given
    // 식별자 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    //유저 객체 생성
    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of(userId));

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    //when
    UUID result = basicChannelService.createPrivate(request);

    //then
    assertNotNull(result);
    then(channelRepository).should().save(any(Channel.class));
    then(readStatusRepository).should(times(1)).save(any());
    then(userRepository).should().findById(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자가 포함되면 PRIVATE 채널 생성 시 예외가 발생해야 합니다.")
  void create_private_fail_user_not_found() {
    // given
    // 식별자 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of(userId));

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> basicChannelService.createPrivate(request))
        .isInstanceOf(UserNotFoundException.class);
    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("PRIVATE 채널은 수정할 수 없습니다.")
  void update_fail_private_channel() {
    //given
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(channel, "id", channelId);

    ChannelUpdateRequest update =
        new ChannelUpdateRequest(channelId, "채널_수정", "채널 수정 테스트");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    //when, then
    assertThatThrownBy(() -> basicChannelService.update(update))
        .isInstanceOf(PrivateChannelCannotBeUpdatedException.class);
    then(channelRepository).should(never()).saveChannel(any(Channel.class));
  }

  @Test
  @DisplayName("PUBLIC 채널은 정상적으로 생성되어야 합니다.")
  void create_public_success() {
    //given
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("PUBLIC 채널 1", "PUBLIC 1 채널입니다.");

    Channel channel = new Channel(ChannelType.PUBLIC, "PUBLIC 채널 1", "PUBLIC 1 채널입니다.");
    ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    //when
    UUID result = basicChannelService.createPublic(request);

    //then
    assertEquals(channel.getId(), result);
    then(channelRepository).should(times(1)).save(any(Channel.class));
  }

  @Test
  @DisplayName("중복된 PUBLIC 채널 이름으로 생성 시 예외가 발생해야 합니다.")
  void create_public_fail_duplicate_name() {
    //given
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("PUBLIC 채널 1", "PUBLIC 1 채널입니다.");

    given(channelRepository.existsByName(request.name())).willReturn(true);
    //when, then
    assertThatThrownBy(() -> basicChannelService.createPublic(request))
        .isInstanceOf(ChannelDuplicateNameException.class);
    then(channelRepository).should(never()).createChannel(any(Channel.class));
  }

  @Test
  @DisplayName("사용자 ID로 참여 중인 채널 목록을 정상적으로 조회해야 합니다.")
  void findAllByUserId_success() {
    //given
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
    // id검색 시 user객체 조회되게 설정
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    //전체 채널 검색 시 channel나오게 설정
    given(channelRepository.findAllChannel()).willReturn(List.of(channel));
    given(channelMapper.toResponse(any(), any(), any())).willReturn(response);
    //when
    List<ChannelResponse> result = basicChannelService.findAllByUserId(userId);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).channelId()).isEqualTo(channelId);
    assertThat(result.get(0).channelName()).isEqualTo("PUBLIC 채널 1");

    then(userRepository).should(times(1)).findById(userId);
    then(channelRepository).should(times(1)).findAllChannel();
    then(channelMapper).should(times(1)).toResponse(any(), any(), any());

  }

  @Test
  @DisplayName("존재하지 않는 사용자 ID로 채널 목록 조회 시 빈 목록 또는 예외를 반환해야 합니다.")
  void findAllByUserId_fail_not_found() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());
    //when, then
    assertThatThrownBy(() -> basicChannelService.findAllByUserId(userId))
        .isInstanceOf(UserNotFoundException.class);

    then(channelRepository).should(never()).findAllChannel();
  }

  @Test
  @DisplayName("채널 정보는 정상적으로 수정되어야 합니다.")
  void update_success() {
    //given
    UUID channelId = UUID.randomUUID();
    Channel channel =
        new Channel(ChannelType.PUBLIC, "PUBLIC 채널 1", "PUBLIC 1 채널입니다.");
    ReflectionTestUtils.setField(channel, "id", channelId);

    ChannelUpdateRequest update =
        new ChannelUpdateRequest(channelId, "채널_수정", "채널 수정 테스트");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    //when
    ChannelResponse result = basicChannelService.update(update);

    // then
    assertThat(result.channelName()).isEqualTo("채널_수정");
    then(channelRepository).should().saveChannel(any(Channel.class));
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    // given
    UUID channelId = UUID.randomUUID();

    // when, then
  }

  @Test
  @DisplayName("채널은 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given

    // when

    // then
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given

    // when, then
  }
}