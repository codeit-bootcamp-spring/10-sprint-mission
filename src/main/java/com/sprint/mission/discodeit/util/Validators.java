package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.Objects;

public class Validators {

    public static void requireNotBlank(String value, String fieldName) {
        requireNonNull(value, fieldName + "은 null이 될 수 없습니다.");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "에 공백을 입력할 수 없습니다.");
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        Objects.requireNonNull(value, fieldName + "은 null이 될 수 없습니다.");
    }

    public static void validationMessage(String content) {
        requireNotBlank(content, "content");
    }

    public static void validationUser(String userName, String userEmail) {
        requireNotBlank(userName, "userName");
        requireNotBlank(userEmail, "userEmail");
    }

    public static void validationChannel(ChannelType type, String channelName, String channelDescription) {
        requireNonNull(type, "type");
        requireNotBlank(channelName, "channelName");
        requireNotBlank(channelDescription, "channelDescription");
    }



}
