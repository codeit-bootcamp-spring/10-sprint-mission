package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;

import java.util.UUID;
import java.util.List;


public interface ChannelService {

  ChannelDto createPublicChannel(PublicChannelCreateRequest request); // PUBLIC 채널 생성

  ChannelDto createPrivateChannel(PrivateChannelCreateRequest request); // PRIVATE 채널 생성

  ChannelDto findById(UUID id); // 단건 조회

  List<ChannelDto> findAllByUserId(UUID userId); // 유저별 참여하고 있는 채널 전체 조회

  ChannelDto update(UUID id, PublicChannelUpdateRequest request); // 체널 정보 수정

  void deleteById(UUID id);
}