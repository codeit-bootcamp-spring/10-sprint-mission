package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    // 저장
    Message save(Message message);

    // 읽기
    Optional<Message> findById(UUID id);

    // 모두 읽기
    List<Message> findAll();

    // 수정
//    Message updateById(UUID id, String content);

    // 삭제
    void deleteById(UUID id);

    // 해당 user Id를 가진 유저가 작성한 메시지 목록을 반환
    List<Message> getMessagesByUserId(UUID userId);

    // 해당 channel Id를 가진 채널의 메시지 목록을 반환
    List<Message> getMessagesByChannelId(UUID channelId);

    void loadData();

    void saveData();
}
