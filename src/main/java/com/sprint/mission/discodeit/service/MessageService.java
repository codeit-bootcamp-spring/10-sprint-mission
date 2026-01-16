package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

// [ ] 검토 필요
public interface MessageService {
    // Create
    /**
     * 메시지 생성 / 메시지 보내기 (특정 채널에 유저가 새로운 메시지를 작성)
     * @param content   메시지 내용 (null 또는 공백 불가)
     * @param userId    작성자(User)의 UUID (존재하는 유저여야 함)
     * @param channelId 메시지가 등록될 채널(Channel)의 UUID (존재하는 채널이어야 함)
     * @return 생성된 Message 객체
     * @throws IllegalArgumentException 내용이 비어있거나, 유저 또는 채널을 찾을 수 없을 때 발생
     */
    Message createMessage(String content, UUID userId, UUID channelId);

    // Read
    /**
     * 메시지 단건 조회
     * 메시지 ID를 통해 특정 메시지의 상세 내용을 조회합니다.
     * @param messageId 조회할 메시지의 UUID
     * @return 조회된 Message 객체
     * @throws IllegalArgumentException 해당 ID의 메시지가 존재하지 않을 경우 발생
     */
    Message findMessageById(UUID messageId);

    /**
     * 특정 채널의 특정메시지 조회 (특정 채널 내에 작성된 모든 메시지를 리스트로 반환)
     * @param channelId 조회할 채널의 UUID
     * @return 해당 채널의 메시지 리스트 (메시지가 없으면 빈 리스트)
     * @throws IllegalArgumentException 해당 ID의 채널이 존재하지 않을 경우 발생할 수 있음
     */
    List<Message> findAllMessagesByChannelId(UUID channelId);

    // Update
    /**
     * 메시지 수정
     * 기존 메시지의 내용을 새로운 내용으로 변경합니다.
     * @param messageId  수정할 대상 메시지의 UUID
     * @param newContent 변경할 새로운 메시지 내용 (null 또는 공백 불가)
     * @return 수정된 Message 객체
     * @throws IllegalArgumentException 메시지가 없거나, 변경할 내용이 유효하지 않을 경우 발생
     */
    Message updateMessage(UUID messageId, String newContent);

    // Delete
    /**
     * 메시지 삭제
     * 특정 메시지 하나를 삭제합니다.
     * @param messageId 삭제할 메시지의 UUID
     * @throws IllegalArgumentException 해당 ID의 메시지가 존재하지 않을 경우 발생
     */
    void deleteMessage(UUID messageId);

    /**
     * 특정 유저가 작성한 모든 메시지 삭제
     * 유저가 탈퇴할 때 호출되며, 해당 유저가 시스템 전체에 남긴 모든 메시지를 일괄 삭제
     * @param userId 탈퇴하는 유저(User)의 UUID
     */
    void deleteAllMessagesByUserId(UUID userId);
}