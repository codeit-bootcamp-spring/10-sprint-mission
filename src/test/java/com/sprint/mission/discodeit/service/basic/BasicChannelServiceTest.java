package com.sprint.mission.discodeit.service.basic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.ChannelDetailResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.factory.MapperContainer;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Spy
  private ReadStatusMapper readStatusMapper = MapperContainer.get(ReadStatusMapper.class);
  @Spy
  private BasicDomainTemplate domainTemplate = new BasicDomainTemplate();
  @Spy
  private ChannelMapper channelMapper = MapperContainer.get(ChannelMapper.class);
  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("등록된 channel 조회를 위해 ChannelRepository.findChannelDetailById 1번 호출")
  void success_to_find_channel() {
    // given
    ChannelDetailResponse channelDetail = ChannelFixture.createChannelDetail();
    given(channelRepository.findChannelDetailById(any(UUID.class)))
        .willReturn(Optional.of(channelDetail));

    // when
    channelService.find(UUID.randomUUID());

    // then
    verify(channelRepository, times(1)).findChannelDetailById(any(UUID.class));
  }

  @Test
  @DisplayName("잘못된 id로 channel 조회 실패하여 ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND)를 던진다")
  void fail_to_find_channel() {
    // given
    UUID incorrectId = UUID.randomUUID();
    given(channelRepository.findChannelDetailById(any(UUID.class)))
        .willReturn(Optional.empty());

    // when & then
    Assertions.assertThatThrownBy(() -> channelService.find(incorrectId))
        .isInstanceOf(ChannelException.class)
        .extracting("errorCode")
        .isEqualTo(ChannelErrorCode.CHANNELID_NOT_FOUND);
  }

  @Test
  @DisplayName("public channel 생성을 위해 ChannelRepository.save 1번 호출")
  void success_to_create_public_channel() {
    // given
    PublicChannelCreateRequest request = ChannelFixture.createPublicRequest();
    given(channelRepository.save(any(Channel.class))).willReturn(any(Channel.class));

    // when
    channelService.createPublic(request);

    // then
    verify(channelRepository, times(1)).save(any(Channel.class));
  }

  @Test
  @DisplayName("""
      private channel 생성을 위해 ChannelRepository.save 1번 호출,
      UserRepository.findProfileAndStatusByIdIn 1번 호출,
      ReadStatusRepository.saveAll 1번 호출""")
  void success_to_create_private_channel() {
    // given
    PrivateChannelCreateRequest request = ChannelFixture.createPrivateRequest();
    Channel channel = mock(Channel.class);
    List<User> participants = UserFixture.createEntities();
    List<ReadStatus> readStatuses = List.of(mock(ReadStatus.class));

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(userRepository.findProfileAndStatusByIdIn(request.getParticipantIds()))
        .willReturn(participants);
    given(readStatusRepository.saveAll(anyList())).willReturn(readStatuses);

    // when
    channelService.createPrivate(request);

    // then
    verify(channelRepository, times(1)).save(any(Channel.class));
    verify(userRepository, times(1)).findProfileAndStatusByIdIn(anyList());
    verify(readStatusRepository, times(1)).saveAll(anyList());
  }
}
