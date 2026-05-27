package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {
    // 필드
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType type;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // Public Channel 생성자
    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        type = ChannelType.PUBLIC;
    }

    // Private Channel 생성
    public static Channel createPrivateChannel(){
        Channel channel = new Channel();
        channel.type = ChannelType.PRIVATE;
        return channel;
    }

    // setter
    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt();
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
