package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.Objects;

public class Validators {

    public static void requireNotBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + "은 null이 될 수 없습니다.");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "에 공백을 입력할 수 없습니다.");
        }
    }

    public static void validationMessage(String content) {
        requireNotBlank(content, "content");
    }

    public static void validationUser(String userName, String userEmail, String userPassword) {
        requireNotBlank(userName, "userName");
        requireNotBlank(userEmail, "userEmail");
        requireNotBlank(userPassword, "userPassword");
    }

    public static void validationChannel(ChannelType type, String channelName, String channelDescription) {
        Objects.requireNonNull(type, "type은 null이 될 수 없습니다.");
        requireNotBlank(channelName, "channelName");
        requireNotBlank(channelDescription, "channelDescription");
    }


}
