package com.sprint.mission.discodeit.validation;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class ValidationMethods {
    // 통합 ID `null` 검증
    public static void validateId(UUID id) {
        Objects.requireNonNull(id, "ID가 null입니다.");
    }

    // requestId와 targetId가 동일한지 검증
    public static void validateSameId(UUID requestId, UUID targetId) {
        if (!requestId.equals(targetId)) {
            throw new IllegalStateException("본인의 정보만 수정 가능합니다.");
        }
    }

    // String `null` or `blank` 검증
    // User: email이나 password, partialName 등
    // Channel: channelName, partialChannelName 등
    // Message: content 등
    public static void validateNullBlankString(String str, String fieldName) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(fieldName + "이(가) 입력되지 않았습니다.");
        }
    }

    // 객체 null 검증
    public static void validateNullObject(Object obj, String objectName) {
        String message = objectName + "가 null 입니다.";
        Objects.requireNonNull(obj, message);
    }

    // 존재하는 유저인지 확인
    public static void existUser(User user) {
        if (user == null) {
            throw new NoSuchElementException("해당 사용자가 없습니다.");
        }
    }

    // 존재하는 채널인지 확인
    public static void existChannel(Channel channel) {
        if (channel == null) {
            throw new NoSuchElementException("해당 채널이 없습니다.");
        }
    }

    // 존재하는 메세지인지 확인
    public static void existMessage(Message message) {
        if (message == null) {
            throw new NoSuchElementException("해당 메세지가 없습니다.");
        }
    }

//    // 빈 상자([]) 출력하지 않을 때
//    // userId로 요청한 데이터가 존재하는지 검증
//    public static void validateDataByUserId(Map<UUID, User> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new NoSuchElementException("해당 userId를 가진 User가 존재하지 않습니다.");
//        }
//    }
//
//    // channelId로 요청한 데이터가 존재하는지 검증
//    public static void validateDataByChannelId(Map<UUID, Channel> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new NoSuchElementException("해당 channelId를 가진 Channel이 존재하지 않습니다.");
//        }
//    }
//
//    // messageId로 요청한 데이터가 존재하는지 검증
//    public static void validateDataByMessageId(Map<UUID, Message> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new NoSuchElementException("해당 messageId를 가진 Message가 존재하지 않습니다.");
//        }
//    }

    // password 규칙? 8자이상, 특수기호

    // email 규칙? @ 포함

    // password 해시?
}
