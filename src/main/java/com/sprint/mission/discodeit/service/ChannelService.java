package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // Create
    /**
     * 채널 생성
     * @param channelName 채널 이름 (필수)
     * @return 생성된 채널 객체
     */
    Channel createChannel(String channelName, UUID ownerId);

    // Read
    /**
     * 채널 단건 조회
     * @param channelId 찾을 채널의 UUID
     * @return 채널이 존재하면 Channel
     */
    Channel findChannelById(UUID channelId); // 메서드 네이밍 변경
    /**
     * 모든 채널 조회
     * @return 채널 리스트 (없으면 빈 리스트)
     */
    List<Channel> findAllChannels();

    // Update
    /**
     * 채널 이름 수정
     * @param channelId 대상 채널 ID
     * @param newChannelName 변경할 새 이름
     * @return 수정된 채널 객체
     */
    Channel updateChannel(UUID channelId, String newChannelName);

    // Delete
    /**
     * 채널 삭제
     * @param channelId 삭제할 채널 ID
     */
    void deleteChannel(UUID channelId);

    void deleteChannelsByOwnerId(UUID ownerId);  // User-Delete 때문에 필요 (추후 구현)
}