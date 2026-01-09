package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    /**
     * 채널 생성
     * @param name 채널 이름 (필수)
     * @return 생성된 채널 객체
     */
    Channel createChannel(String name);

    /**
     * 채널 단건 조회
     * @param id 찾을 채널의 UUID
     * @return 채널이 존재하면 Optional<Channel>, 없으면 Optional.empty()
     */
    Optional<Channel> findOne(UUID id);

    /**
     * 모든 채널 조회
     * @return 채널 리스트 (없으면 빈 리스트)
     */
    List<Channel> findAll();

    /**
     * 채널 이름 수정
     * @param id 대상 채널 ID
     * @param newName 변경할 새 이름
     * @return 수정된 채널 객체
     */
    Channel updateChannel(UUID id, String newName);

    /**
     * 채널 삭제
     * @param id 삭제할 채널 ID
     */
    void deleteChannel(UUID id);
}