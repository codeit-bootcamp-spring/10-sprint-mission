package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    PUBLIC(0), PRIVATE(1);

    private final int value;

    ChannelType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChannelType fromValue(int value) {
        for (ChannelType type : ChannelType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for [" + value + "]");
    }
}
