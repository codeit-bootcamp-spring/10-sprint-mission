package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Getter
@ToString(exclude = {"members", "messages"})
public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private ChannelType type;
    private ChannelVisibility visibility;   // 채널 공개/비공개

    @Getter(AccessLevel.NONE)
    private final List<User> members = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private final List<Message> messages = new ArrayList<>();

    public Channel(String name, String description, ChannelType type, ChannelVisibility visibility) {
        super();
        this.name = name;
        this.description = description;
        this.type = (type != null) ? type : ChannelType.TEXT;
        this.visibility = visibility;
    }

    // 채널 이름 수정
    public void updateName (String name) {
        this.name = name;
        this.updated();
    }

    // 채널 설명 수정
    public void updateDescription (String description) {
        this.description = description;
        this.updated();
    }

    // 채널 공개 여부 수정
    public void updateVisibility(ChannelVisibility visibility) {
        if (visibility != null) {
            this.visibility = visibility;
            this.updated();
        }
    }
}
