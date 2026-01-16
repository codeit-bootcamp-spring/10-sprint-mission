package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // Create
    /**
     * 채널 생성 / 채널장(서버장)인 Owner 필수 (유저 없는 채널 존재 불가능)
     * @param channelName 채널 이름
     * @param owner 채널장(Owner) 객체
     * @return 생성된 채널 객체
     * throws 구현 필요
     */
    Channel createChannel(String channelName, User owner);

    // Read
    /**
     * 채널 단건 조회 / 특정 채널 조회 (채널 가입 등을 위한 검색)
     * @param channelId 찾을 채널의 UUID
     * @return 채널이 존재하면 Channel
     * throws 구현 필요
     */
    Channel findChannelById(UUID channelId); // 메서드 네이밍 변경

    /**
     * 모든 채널 조회 / 전체 채널 조회 (채널 가입 등을 위한 탐색창)
     * @return 채널 리스트 (없으면 빈 리스트)
     * throws 구현 필요
     */
    List<Channel> findAllChannels();

    // Update
    /**
     * 채널 이름 수정
     * @param channelId 수정 대상 채널 ID
     * @param newChannelName 변경할 새 채널
     * @return 수정된 채널 객체
     * throws 구현 필요
     */
    Channel updateChannel(UUID channelId, String newChannelName);

    // Delete
    /**
     * 채널 삭제 / 채널 내 메시지 + 채널-유저 관계 해제
     * @param channelId 삭제할 채널 ID
     * throws 구현 필요
     */
    void deleteChannel(UUID channelId);

    /**
     * 특정 채널장의 모든 채널 삭제
     * 특정 유저가 삭제될 때, 그 유저가 소유한(Owner) 모든 채널을 일괄 삭제
     * (실제로는 Owner가 나갈 경우 다른 사람에게 소유권을 전달해야 나갈 수 있음 - 추후 구현)
     * @param ownerId 탈퇴하는 채널장(User)의 UUID
     * throws 구현 필요
     */
    void deleteChannelsByOwnerId(UUID ownerId);  // User-Delete 때문에 필요
}