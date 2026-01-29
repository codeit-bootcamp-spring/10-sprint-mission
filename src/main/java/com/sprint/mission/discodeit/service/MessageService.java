package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성 (하지만 실제로는 객체를 등록하는 역할)
    Message createMessage(String content, UUID userId, UUID channelID);


    // 메세지 조회(시스템) ID로!
    // id(UUID)로 특정 메시지 찾기 — 내부 시스템용
    MessageResponse getMessageResponseByID(UUID id);
    // 메세지 전체 조회 -> 리스트로 역시.
    List<MessageResponse> getAllMessagesResponse();


    //    (시스템) id로 메세지 조회
    //    Message getMessageById(UUID id);

    // 특정 사용자가 보낸 메시지 조회(사용자끼리)
    List<MessageResponse> getMessageResponsesBySenderId(UUID senderId);

    // 특정 채널에서의 메세지 리스트 조회
    List<MessageResponse> getMessageResponsesInChannel(UUID channelId);


    Message updateMessage(UUID uuid, String newContent); // 메시지 내용 변경


    void deleteMessage(UUID id); // id로 메시지 삭제

    // 조회는 -> DTO로 제공!!





}


