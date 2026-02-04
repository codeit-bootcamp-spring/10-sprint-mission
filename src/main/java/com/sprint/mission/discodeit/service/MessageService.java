package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성 -> DTO로 그룹화
    MessageResponse createMessage(MessageCreateRequest request);

    // 메세지 조회(시스템) ID로!
    // 단건 조회!!!
    MessageResponse findMessageResponseByID(UUID id);


    // 특정 채널의 메세지 전체 조회 -> 리스트로 역시.
    List<MessageResponse> findAllByChannelId(UUID channelId);

    // 변경
    MessageResponse updateMessage(MessageUpdateRequest request); // 메시지 내용 변경

    // 삭제
    void deleteMessage(UUID messageid); // id로 메시지 삭제

    // 조회는 -> DTO로 제공!!

    // 특정 사용자가 보낸 메시지 조회(사용자끼리)
    //List<MessageResponse> getMessageResponsesBySenderId(UUID senderId);

    // 특정 채널에서의 메세지 리스트 조회
    //List<MessageResponse> getMessageResponsesInChannel(UUID channelId);




}


