package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    void createChannel(Channel channel);

    // 읽기
    Channel findById(UUID id);

    // 모두 읽기
    List<Channel> findAll();

    // 수정
    void updateById(UUID id, String newChannelName);

    // 삭제
    void deleteById(UUID id);

    void printAllChannels();

}
