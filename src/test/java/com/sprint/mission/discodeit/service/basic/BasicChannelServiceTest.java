package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotUpdatableException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("Public 채널 생성 성공")
  void createPublicChannel_Success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("testChannel",
        "description");
    given(channelRepository.existsByName(anyString())).willReturn(false);

    // when
    channelService.createPublicChannel(request);

    // then
    then(channelRepository).should().save(any(Channel.class));
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  @DisplayName("Public 채널 생성 실패 - 중복된 채널 이름")
  void createPublicChannel_Fail_DuplicateName() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("duplicateName", null);
    given(channelRepository.existsByName(request.getName())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> channelService.createPublicChannel(request))
        .isInstanceOf(DuplicateChannelNameException.class);
  }

  @Test
  @DisplayName("Private 채널 생성 성공")
  void createPrivateChannel_Success() {
    // given
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest();
    request.setCreatorId(userId);
    request.setParticipantIds(Collections.emptyList());
    User user = new User("testUser", "test@email.com", "password", null);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    channelService.createPrivateChannel(request);

    // then
    then(channelRepository).should().save(any(Channel.class));
    then(readStatusRepository).should().save(any());
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  @DisplayName("Private 채널 생성 실패 - 참여자 중 존재하지 않는 유저 포함")
  void createPrivateChannel_Fail_ParticipantNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest();
    request.setCreatorId(userId);
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.createPrivateChannel(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("채널 수정 성공")
  void updateChannel_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();
    request.setNewName("newName");
    request.setNewDescription("newDesc");
    Channel channel = new Channel("oldName", ChannelType.PUBLIC, "oldDesc");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when
    channelService.updateChannel(channelId, request);

    // then
    assertThat(channel.getName()).isEqualTo(request.getNewName());
    assertThat(channel.getDescription()).isEqualTo(request.getNewDescription());
    then(channelMapper).should().toDto(channel);
  }

  @Test
  @DisplayName("채널 수정 실패 - Private 채널 수정 시도")
  void updateChannel_Fail_PrivateChannelNotUpdatable() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();
    Channel channel = new Channel("privateChannel", ChannelType.PRIVATE, "");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when & then
    assertThatThrownBy(() -> channelService.updateChannel(channelId, request))
        .isInstanceOf(PrivateChannelNotUpdatableException.class);
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel("channel", ChannelType.PUBLIC, "");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when
    channelService.deleteChannel(channelId);

    // then
    then(channelRepository).should().delete(channel);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
  void deleteChannel_Fail_ChannelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.deleteChannel(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("유저 ID로 참여 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);
    given(channelRepository.findAll()).willReturn(Collections.emptyList());

    // when
    List<com.sprint.mission.discodeit.dto.ChannelDto> result = channelService.findAllByUserId(
        userId);

    // then
    assertThat(result).isNotNull();
    then(channelRepository).should().findAll();
  }

  @Test
  @DisplayName("유저 ID로 참여 채널 목록 조회 실패 - 존재하지 않는 유저")
  void findAllByUserId_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.findAllByUserId(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
