package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.ChannelType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {
    @Column(name = "channel_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private ChannelType type;

    @Column(name = "description")
    private String description;

    public Channel(String channelName, String description) {
        this.name = channelName;
        this.description = description;
    }

    public Channel(String channelName, String description, ChannelType channelType) {
        this.name = channelName;
        this.description = description;
        this.type = channelType;
    }

    public void updateChannelName(String newChannelName) {
        this.name = newChannelName;
    }

    public void updateChannelDescription(String newDescription){
        this.description = newDescription;
    }
}