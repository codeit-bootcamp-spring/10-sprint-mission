package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Channel extends BaseEntity {

    private String channelName;
    private String description;
    private boolean isPrivate;
    private List<User> participants;

    public Channel(String name, String description) {
        super();
        this.channelName = name;
        this.description = description;
        this.isPrivate = false;
        this.participants = new ArrayList<>();
    }

    public Channel(List<User> participants) {
        super();
        this.isPrivate = true;
        this.participants = participants != null ? participants : new ArrayList<>();
    }

    public void updateChannel(String name, String description) {
        if (isPrivate) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        if (name != null) this.channelName = name;
        if (description != null) this.description = description;
        touch();
    }

    public void addParticipant(User user) {
        if (!participants.contains(user)) {
            participants.add(user);
            touch();
        }
    }

    public void removeParticipant(User user) {
        if (participants.remove(user)) {
            touch();
        }
    }
}
