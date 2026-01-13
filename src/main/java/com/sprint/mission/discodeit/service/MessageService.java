package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성 (하지만 실제로는 객체를 등록하는 역할)
    Message createMessage(String content, User sender, Channel ch);


    // 메세지 조회(시스템) ID로!
    // id(UUID)로 특정 메시지 찾기 — 내부 시스템용
    Message getMessageById(UUID id);

    // 메세지 전체 조회 -> 리스트로 역시.
    // 저장된 모든 메시지 목록 반환
    List<Message> getMessageAll();


    Message updateMessage(UUID uuid, String newContent); // 메시지 내용 변경


    void deleteMessage(UUID id); // id로 메시지 삭제


    List<Message> getMessagesByChannelName(String channelName); // 특정 채널에 있는 메시지만 조회(사용자)

    List<Message> getMessagesBySenderName(String senderName); // 특정 사용자가 보낸 메시지 조회(사용자끼리)
}
