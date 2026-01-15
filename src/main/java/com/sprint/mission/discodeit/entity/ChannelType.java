package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    DIRECT("dm"),
    PUBLIC("public"),
    PRIVATE("private");

    private String value;
    ChannelType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    //외부 입력용
    public static ChannelType fromValue(String value) {
        for (ChannelType channelType : ChannelType.values()) {
            if (channelType.getValue().equalsIgnoreCase(value.trim())) {
                return channelType;
            }
        }
        throw new IllegalArgumentException(value + "유효하지 않은 채널 타입");
    }
}
