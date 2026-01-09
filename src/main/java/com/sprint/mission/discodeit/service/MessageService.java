package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.*;

public interface MessageService {
    // Create
    /**
     * 메시지 생성 (보내기)
     * @param content 메시지 내용
     * @param userId 작성자 ID (Foreign Key)
     * @param channelId 채널 ID (Foreign Key)
     * @return 생성된 메시지 객체
     */
    Message createMessage(String content, UUID userId, UUID channelId);

    // Read
    /**
     * 메시지 단건 조회
     * @param messageId 메시지 ID
     * @return 메시지 객체 (Optional)
     */
    Optional<Message> findOne(UUID messageId);

    /**
     * 특정 채널의 메시지 전체 조회
     * 시스템 전체 메시지 조회(findAll)는 데이터가 너무 많아 위험하므로,'채널 별'로 조회하도록 제한
     * @param channelId 조회할 채널의 ID
     * @return 해당 채널의 메시지 리스트 (시간순 정렬 권장)
     */
    List<Message> findAllByChannelId(UUID channelId);

    //
    /**
     * 메시지 수정
     * @param id 수정할 메시지 ID
     * @param newContent 수정 후 내용 (새로운 내용)
     * @return 수정된 메시지 객체
     */
    Message updateMessage(UUID id, String newContent);

    /**
     * 메시지 삭제
     * @param messageId 삭제할 메시지 ID
     */
    void deleteMessage(UUID messageId);
}