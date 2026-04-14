package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.List;


public interface ChannelService {

  Channel createPublicChannel(String name, String description);

  Channel createPrivateChannel(List<UUID> participantIds);

  Channel findById(UUID id);

  List<Channel> findAllByUserId(UUID userId);

  Channel update(UUID id, String newName, String newDescription);

  void deleteById(UUID id);

  // 단건 조회용 (생성/수정에서 사용)
  Instant getLastMessageAt(UUID channelId); //  채널 DTO 변환 시, 마지막 메시지 작성 시간 필드(lastMessageAt)를 위한 메서드

  List<User> getParticipants(UUID channelId); // 채널 DTO 변환 시, 참여자 정보 필드(participants)를 위한 메서드

  // 일괄 조회용 (목록에서 사용)
  Map<UUID, Instant> getLastMessagesAtMap(
      List<UUID> channelIds);

  Map<UUID, List<User>> getParticipantsMap(
      List<UUID> channelIds);
}