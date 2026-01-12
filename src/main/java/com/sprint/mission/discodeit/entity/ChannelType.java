package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    PUBLIC(0), PRIVATE(1);

    private final String value;

    ChannelType(int value) {
        this.value = String.valueOf(value);
    }

    public String getValue() {
        return value;
    }

    public static ChannelType fromValue(String value) {
        return ChannelType.valueOf(value);
    }

    public ChannelType switchType() {
        return this == ChannelType.PUBLIC ? ChannelType.PRIVATE : ChannelType.PUBLIC;
    }
}
