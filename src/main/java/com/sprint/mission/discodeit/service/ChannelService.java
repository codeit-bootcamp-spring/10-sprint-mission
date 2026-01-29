package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);
    //channel 생성 // 생성이라고 표현은 하지만, 실제로는 객체를 등록하는 것이고
    //생성은 나중에 new생성지가 하기 때문에 착각 금지. -> 그래서 반환 타입 void
    //추가 -> 생성자 main x -> 서비스에서 객체 생성까지 담당으로 변경.

    // 각 서비스에 해당 ID가 존재하는지를 !

    List<Channel> getChannelAll();// channel 조회 -> 채널 목록 전부!
    Channel updateChannel(UUID uuid, String newName);// channel 정보 변경
    void deleteChannel(UUID uuid);// channel 삭제


    Channel getChannelByName(String channelName); // channel 조회 (사용자)


    //id로 채널 조회
    Channel findChannelById(UUID id);


    // 추가 ChatCoordinator 분산
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);

}