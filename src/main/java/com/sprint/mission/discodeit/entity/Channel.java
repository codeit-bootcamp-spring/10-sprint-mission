package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@NoArgsConstructor
@Table(name = "channels")
@Getter
public class Channel extends BaseUpdatableEntity {
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(nullable = true, length = 100)
    private String name;

    @Column(nullable = true, length = 500)
    private String description;


    private Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public static Channel of(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    public static Channel of(List<UUID> participantIds) {
        return new Channel(ChannelType.PRIVATE, null, null);
    }


    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}


