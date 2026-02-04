package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    PUBLIC,
    PRIVATE;

    public boolean isPrivate() {
        return this == PRIVATE;
    }

    public boolean isPublic() {
        return this == PUBLIC;
    }
}
