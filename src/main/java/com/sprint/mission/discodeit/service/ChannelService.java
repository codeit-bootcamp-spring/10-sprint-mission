package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);
    //channel 생성 // 생성이라고 표현은 하지만, 실제로는 객체를 등록하는 것이고
    //생성은 나중에 new생성지가 하기 때문에 착각 금지. -> 그래서 반환 타입 void

    Channel getChannelById (UUID uuid);//channel 조회 -> id로...(클래스끼리)
    List<Channel> geyChannelAll();// channel 조회 -> 채널 목록 전부!
    void updateChannel(Channel channel);// channel 정보 변경
    void deleteChannel(UUID uuid);// channel 삭제


    Channel getChannelByName(String channelName); // channel 조회 (사용자)
}
