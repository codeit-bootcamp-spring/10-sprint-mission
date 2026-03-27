package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
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

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  ChannelRepository channelRepository;

  @Mock
  ReadStatusRepository readStatusRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  ChannelMapper channelMapper;

  @InjectMocks
  BasicChannelService basicChannelService;

  @Test
  @DisplayName("공개 채널 생성에 성공해야 한다.")
  void should_create_public_channel() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("일반 채널", "일반 채널입니다.");
    Channel expectedChannel = new Channel(ChannelType.PUBLIC, "일반 채널", "일반 채널입니다.");

    given(channelMapper.toEntity(request)).willReturn(
        expectedChannel);
    given(channelRepository.save(any(Channel.class))).willReturn(expectedChannel);
    // when
    basicChannelService.createPublic(request);
    // then
    then(channelRepository).should(times(1)).save(expectedChannel);
  }

  @Test
  @DisplayName("요청이 null이라면 공개 채널 생성에 실패해야 한다.")
  void should_fail_to_create_public_channel_when_request_is_null() {
    // given
    PublicChannelCreateRequest request = null;

    // when, then
    assertThrows(NullPointerException.class, () -> {
      basicChannelService.createPublic(request);
    });
    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성에 성공해야 한다.")
  void should_create_private_channel() {
    // given
    List<UUID> fixedParticipantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    User fixedUser = new User("가짜 유저", "fake@email.com", "12", null);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(fixedParticipantIds);
    Channel expectedChannel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelMapper.toEntity(request)).willReturn(
        expectedChannel);
    given(userRepository.findById(any())).willReturn(Optional.of(fixedUser));
    given(channelRepository.save(any(Channel.class))).willReturn(expectedChannel);
    // when
    basicChannelService.createPrivate(request);
    // then
    then(readStatusRepository).should(times(fixedParticipantIds.size()))
        .save(any(ReadStatus.class));
    then(channelRepository).should(times(1)).save(expectedChannel);
  }

  @Test
  @DisplayName("요청이 null이라면 비공개 채널 생성에 실패해야 한다.")
  void should_fail_to_create_private_channel_when_request_is_null() {
    // given
    PrivateChannelCreateRequest request = null;

    // when, then
    assertThrows(NullPointerException.class, () -> {
      basicChannelService.createPrivate(request);
    });
    then(channelRepository).should(never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("유저의 Id로 참여중인 채널을 조회에 성공해야 한다.")
  void should_find_all_channel_by_user_id() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel channel1 = new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다.");
    Channel channel2 = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findAllByUserId(fixedUuid)).willReturn(List.of(channel1, channel2));

    // when
    List<ChannelDto> allByUserId = basicChannelService.findAllByUserId(fixedUuid);

    // then
    assertEquals(2, allByUserId.size());
    then(channelMapper).should(times(2)).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("유저가 참여중인 채널이 없으면 빈 리스트가 반환되야 한다.")
  void should_return_empty_list_when_user_has_no_channels() {
    // given
    UUID fixedUuid = UUID.randomUUID();

    given(channelRepository.findAllByUserId(fixedUuid)).willReturn(List.of());

    // when
    List<ChannelDto> allByUserId = basicChannelService.findAllByUserId(fixedUuid);

    // then
    assertEquals(0, allByUserId.size());
    then(channelMapper).should(never()).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("Public 채널은 수정사항이 반영되어야 한다.")
  void should_update_channel() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel existingChannel = new Channel(ChannelType.PUBLIC, "옛날 채널", "옛날 채널입니다.");
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 채널", "새로운 채널입니다.");

    given(channelRepository.findById(fixedUuid)).willReturn(Optional.of(existingChannel));
    // when
    basicChannelService.update(fixedUuid, request);
    // then
    assertEquals("새로운 채널", existingChannel.getName());
    assertEquals("새로운 채널입니다.", existingChannel.getDescription());
  }

  @Test
  @DisplayName("Private 채널은 수정에 실패해야 한다.")
  void should_fail_update_channel_when_type_is_private() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel existingChannel = new Channel(ChannelType.PRIVATE, null, null);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 채널", "새로운 채널입니다.");

    given(channelRepository.findById(fixedUuid)).willReturn(Optional.of(existingChannel));
    // when, then
    assertThrows(PrivateChannelUpdateException.class, () -> {
      basicChannelService.update(fixedUuid, request);
    });
    assertNull(existingChannel.getName());
    assertNull(existingChannel.getDescription());
  }

  @Test
  @DisplayName("채널 삭제에 성공해야 한다.")
  void should_delete_channel() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel expectedChannel = new Channel(ChannelType.PUBLIC, "삭제 채널", "삭제 채널입니다.");

    given(channelRepository.findById(fixedUuid)).willReturn(Optional.of(expectedChannel));
    // when
    basicChannelService.delete(fixedUuid);
    // then
    then(channelRepository).should(times(1)).delete(expectedChannel);
  }

  @Test
  @DisplayName("삭제하려는 채널이 존재하지 않으면 삭제에 실패해야 한다.")
  void should_fail_delete_channel_when_channel_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();

    given(channelRepository.findById(fixedUuid)).willReturn(Optional.empty());
    // when, then
    assertThrows(ChannelNotFoundException.class, () -> {
      basicChannelService.delete(fixedUuid);
    });
    then(channelRepository).should(never()).delete(any(Channel.class));
  }
}