package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Nested
  @DisplayName("채널 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: PUBLIC 채널이 정상적으로 생성된다")
    void create_Success_PublicChannel() {
      // given
      UUID ownerId = UUID.randomUUID();
      String channelName = "공개 채널";
      String channelDescription = "공개 채널 설명";

      Channel mockChannel = new Channel(channelName, channelDescription,
          ChannelType.PUBLIC); // 가짜 공개 채널 생성
      given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);

      // when
      Channel result = channelService.createPublicChannel(channelName, channelDescription);

      // then
      assertThat(result.getName()).isEqualTo(channelName);
      assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
      then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("성공: PRIVATE 채널이 정상적으로 생성되고, 참여자의 ReadStatus가 저장된다")
    void create_Success_PrivateChannel() {
      // given
      UUID userId = UUID.randomUUID();
      List<UUID> participantIds = List.of(userId);

      Channel mockChannel = new Channel(null, null, ChannelType.PRIVATE); // 가짜 비공개 채널 생성
      User mockUser = new User("tester", "test@test.com", "pw", null); // 가짜 유저 생성

      given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
      given(userRepository.findById(userId)).willReturn(java.util.Optional.of(mockUser));

      // when
      Channel result = channelService.createPrivateChannel(participantIds);

      // then
      assertThat(result.getType()).isEqualTo(ChannelType.PRIVATE);
      then(channelRepository).should().save(any(Channel.class));
      then(readStatusRepository).should().save(any()); // ReadStatus 저장 호출 확인
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저가 채널을 생성하려 하면 UserNotFoundException이 발생한다")
    void createPrivateChannel_Fail_UserNotFound() {
      // given
      UUID invalidUserId = UUID.randomUUID();
      List<UUID> participantIds = List.of(invalidUserId);

      given(userRepository.findById(invalidUserId)).willReturn(
          java.util.Optional.empty()); // getOrThrowUser에서 예외가 터지도록 설정

      // when & then
      assertThrows(UserNotFoundException.class, () -> {
        channelService.createPrivateChannel(participantIds);
      });
      then(channelRepository).should(never()).save(any(Channel.class)); // 실패했으니 채널이 저장되면 안 됨을 검증
    }
  }

  @Nested
  @DisplayName("채널 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 존재하는 채널의 이름과 설명을 정상적으로 수정한다")
    void update_Success() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel existingChannel = new Channel("구 이름", "구 설명", ChannelType.PUBLIC);

      given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));

      // when
      Channel result = channelService.update(channelId, "새 이름", "새 설명");

      // then
      assertThat(result.getName()).isEqualTo("새 이름");
      assertThat(result.getDescription()).isEqualTo("새 설명");
      then(channelRepository).should().findById(channelId);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채널을 수정하려 하면 ChannelNotFoundException이 발생한다")
    void update_Fail_NotFound() {
      // given
      UUID channelId = UUID.randomUUID();
      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      // when & then
      assertThrows(ChannelNotFoundException.class, () -> {
        channelService.update(channelId, "새 이름", "새 설명");
      });
    }

    @Test
    @DisplayName("실패: PRIVATE 채널을 수정하려 하면 PrivateChannelUpdateException이 발생한다")
    void update_Fail_PrivateChannel() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel mockChannel = new Channel(null, null, ChannelType.PRIVATE); // 가짜 비공개 채널 생성

      given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

      // when & then
      assertThrows(PrivateChannelUpdateException.class, () -> {
        channelService.update(channelId, "새 이름", "새 설명");
      });
    }
  }

  @Nested
  @DisplayName("채널 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 존재하는 채널을 정상적으로 삭제한다")
    void delete_Success() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel existingChannel = new Channel("삭제할 방", "설명", ChannelType.PUBLIC);

      given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));

      // when
      channelService.deleteById(channelId);

      // then
      then(channelRepository).should().delete(existingChannel);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채널을 삭제하려 하면 ChannelNotFoundException이 발생한다")
    void delete_Fail_NotFound() {
      // given
      UUID channelId = UUID.randomUUID();
      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      // when & then
      assertThrows(ChannelNotFoundException.class, () -> {
        channelService.deleteById(channelId);
      });
      then(channelRepository).should(never()).delete(any(Channel.class)); // 삭제 로직이 호출되지 않았음을 검증
    }
  }

  @Nested
  @DisplayName("유저별 채널 목록 조회 테스트")
  class FindByUserIdTest {

    @Test
    @DisplayName("성공: 특정 유저가 속한 채널 목록을 정상적으로 반환한다")
    void findByUserId_Success() {
      // given
      UUID userId = UUID.randomUUID();

      // 가짜 공개 채널 리스트
      Channel publicChannel = new Channel("공개 채널", "설명", ChannelType.PUBLIC);
      List<Channel> mockPublicChannels = List.of(publicChannel);

      // 가짜 비공개 채널 리스트
      Channel privateChannel = new Channel(null, null, ChannelType.PRIVATE);
      User mockUser = new User("tester", "test@test.com", "pw", null);
      ReadStatus mockReadStatus = new ReadStatus(mockUser, privateChannel, java.time.Instant.now());
      List<ReadStatus> mockReadStatuses = List.of(mockReadStatus);

      // 채널 모킹
      given(channelRepository.findByType(ChannelType.PUBLIC)).willReturn(mockPublicChannels);
      given(readStatusRepository.findAllByUserIdWithChannel(userId)).willReturn(mockReadStatuses);

      // when
      List<Channel> result = channelService.findAllByUserId(userId);

      // then
      assertThat(result.size()).isEqualTo(2);

      // 채널 타입별로 잘 포함되어있는지 확인
      long publicCount = result.stream().filter(c -> c.getType() == ChannelType.PUBLIC).count();
      long privateCount = result.stream().filter(c -> c.getType() == ChannelType.PRIVATE).count();
      assertThat(publicCount).isEqualTo(1);
      assertThat(privateCount).isEqualTo(1);

      // 각 채널이 해당 타입 채널인지 확인 (메서드 호출 여부 검증)
      then(channelRepository).should().findByType(ChannelType.PUBLIC);
      then(readStatusRepository).should().findAllByUserIdWithChannel(userId);
    }
  }
}