package com.sprint.mission.discodeit.entity.status;

public enum UserStatusType {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
    AWAY("AWAY");

    private final String value;

    UserStatusType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}