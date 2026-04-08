package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.ChannelSummary;
import com.sprint.mission.discodeit.dto.ChannelDto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateNameException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotEditableException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
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
  ChannelRepository channelRepository;
  @Mock
  ReadStatusRepository readStatusRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ChannelMapper channelMapper;
  @Mock
  UserMapper userMapper;

  @InjectMocks
  BasicChannelService channelService;

  @Nested
  class createPublicChannel {

    @Test
    @DisplayName("채널이름, 설명으로 channelDto 반환")
    void should_return_channelDto_with_create_request() {
      // given
      PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널명", "설명");
      ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, request.name(),
          request.description(), null, null);
      given(channelRepository.existsByName(request.name())).willReturn(false);
      given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

      // when
      ChannelDto actualDto = channelService.createChannel(request);

      // then
      assertEquals(expectedDto.name(), actualDto.name());
      assertEquals(expectedDto.description(), actualDto.description());
    }

    @Test
    @DisplayName("중복된 채널이름이 있으면 에러 반환")
    void should_throw_exception_when_duplicate_name() {
      // given
      PublicChannelCreateRequest request = new PublicChannelCreateRequest("채널명", "설명");
      ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, request.name(),
          request.description(), null, null);
      given(channelRepository.existsByName(request.name())).willReturn(true);

      // when, then
      assertThrows(DuplicateNameException.class, () -> channelService.createChannel(request));
      then(channelRepository).should(never()).save(any(Channel.class));
    }

  }

  @Nested
  class createPrivateChannel {

    @Test
    @DisplayName("참가자 id로 channelDto 반환")
    void should_return_channelDto_when_participantIds() {
      // given
      List<UUID> participants = List.of(UUID.randomUUID(), UUID.randomUUID());
      List<User> users = List.of(
          new User("u1", "1", "u1@gmail.com"),
          new User("u2", "2", "u2@gmail.com"));
      given(userRepository.findAllById(participants)).willReturn(users);

      given(readStatusRepository.saveAll(anyList())).willReturn(List.of());

      ChannelDto expectedDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE,
          null, null, List.of(), null);
      given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

      // when
      ChannelDto actualDto = channelService.createChannel(
          new PrivateChannelCreateRequest(participants));

      // then
      assertNotNull(actualDto);
      then(channelRepository).should().save(any(Channel.class));
      then(readStatusRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("참가자 id목록으로 유저를 전부 찾을 수 없는 경우 에러 반환")
    void should_throw_exception_when_some_participants_not_found() {
      // given
      List<UUID> participants = List.of(UUID.randomUUID(), UUID.randomUUID());
      given(userRepository.findAllById(participants)).willReturn(Collections.emptyList());

      // when, then
      assertThrows(UserNotFoundException.class, () ->
          channelService.createChannel(new PrivateChannelCreateRequest(participants)));
    }
  }

  @Nested
  class updateChannel {

    @Test
    @DisplayName("채널명, 설명으로 바꾼 channelDto 반환")
    void should_return_channelDto_when_fields_are_provided() {
      // given
      String newName = "new name";
      String newDescription = "new description";
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName, newDescription);
      Channel existingPublicChannel = Channel.of("old name", "old description");
      ChannelDto expectedDto = new ChannelDto(null, ChannelType.PUBLIC,
          newName, newDescription, null, null);

      given(channelRepository.findById(any(UUID.class)))
          .willReturn(Optional.of(existingPublicChannel));
      given(channelRepository.existsByName(anyString())).willReturn(false);
      given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

      // when
      ChannelDto actualDto = channelService.updateChannel(UUID.randomUUID(), request);

      // then
      assertEquals(expectedDto.name(), actualDto.name());
      assertEquals(expectedDto.description(), actualDto.description());
      then(channelRepository).should().save(existingPublicChannel);
    }

    @Test
    @DisplayName("채널 id로 해당하는 채널을 찾을 수 없어 에러 반환")
    void should_throw_exception_when_channel_not_found() {
      // given
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(null, null);
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(ChannelNotFoundException.class, () ->
          channelService.updateChannel(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("채널 타입이 PRIVATE이면 에러 반환")
    void should_throw_exception_when_updating_private_channel() {
      // given
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(null, null);
      Channel channel = Channel.of(Collections.emptyList());    // Private Channel
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));

      // when, then
      assertThrows(PrivateChannelNotEditableException.class, () ->
          channelService.updateChannel(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("중복된 채널명이 있으면 에러 반환")
    void should_throw_exception_when_duplicate_name() {
      // given
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", null);
      Channel channel = Channel.of(null, null);    // Public Channel
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));
      given(channelRepository.existsByName(anyString())).willReturn(true);

      // when, then
      assertThrows(DuplicateNameException.class, () ->
          channelService.updateChannel(UUID.randomUUID(), request));
    }
  }

  @Nested
  class deleteChannel {

    @Test
    @DisplayName("채널이 존재할 경우 성공적으로 삭제")
    void should_delete_channel_when_id_exists() {
      // given
      Channel channel = Channel.of(null, null);
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));

      // when
      channelService.deleteChannel(UUID.randomUUID());

      // then
      then(channelRepository).should().deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("채널이 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_channel_not_found() {
      // given
      given(channelRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(ChannelNotFoundException.class, () ->
          channelService.deleteChannel(UUID.randomUUID()));
    }
  }

  @Nested
  class findAllByUserId {

    @Test
    @DisplayName("유저가 존재할 경우 참여한 채널을 찾아서 List<ChannelDto> 반환")
    void should_return_List_channelDto_when_userId_exists() {
      // given
      UUID publicChannelId = UUID.randomUUID();
      UUID privateChannelId = UUID.randomUUID();

      User user1 = new User("A", "AA", "A@gmail.com");
      User user2 = new User("B", "BB", "B@outlook.com");

      List<ChannelSummary> summaries = List.of(
          new ChannelSummary(publicChannelId, ChannelType.PUBLIC, null, null, null),
          new ChannelSummary(privateChannelId, ChannelType.PRIVATE, null, null, null));

      Channel mockPrivateChannel = mock(Channel.class);
      ReadStatus readStatus1 = new ReadStatus(user1, mockPrivateChannel);
      ReadStatus readStatus2 = new ReadStatus(user2, mockPrivateChannel);
      given(mockPrivateChannel.getId()).willReturn(privateChannelId);

      UserDto userDto = new UserDto(null, null, null, null, null);

      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user1));
      given(channelRepository.findAllByUserId(any(UUID.class))).willReturn(summaries);
      given(readStatusRepository.findAllByChannelIdIn(anyList()))
          .willReturn(List.of(readStatus1, readStatus2));
      given(userMapper.toDto(any(User.class))).willReturn(userDto);

      UserDto userDto1 = new UserDto(null, "A", "A@gmail.com", null, null);
      UserDto userDto2 = new UserDto(null, "B", "B@outlook.com", null, null);
      List<ChannelDto> expectedDtos = List.of(
          new ChannelDto(publicChannelId, ChannelType.PUBLIC, null, null, null, null),
          new ChannelDto(privateChannelId, ChannelType.PRIVATE, null, null,
              List.of(userDto1, userDto2), null));
      given(channelMapper.toDto(anyList(), anyMap())).willReturn(expectedDtos);

      // when
      List<ChannelDto> actualDtos = channelService.findAllByUserId(UUID.randomUUID());

      // then
      assertThat(actualDtos)
          .hasSize(2)
          .filteredOn(c -> c.type() == ChannelType.PRIVATE)
          .flatExtracting(ChannelDto::participants)
          .hasSize(2);
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_user_not_found() {
      // given
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.empty());

      // when, then
      assertThrows(UserNotFoundException.class, () ->
          channelService.findAllByUserId(UUID.randomUUID()));
    }
  }
}