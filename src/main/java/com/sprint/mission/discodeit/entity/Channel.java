package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private final User owner;  // 객체 참조로 수정

    // 생성할 때 무조건 사람이 있는 상태로 시작해야 되는가? -> 아무도 없는 채널을 만드는 건 이상함
    // 생성할 때 생성자 본인은 있어야 함 -> ownderId 받아서 채널 관리자로 설정 필요
    //     그럼 User 객체에 Enum role 선언해서 User별 상태를 따로 관리해줘야 하는가?
    //     채널별로 ADMIN과 USER를 나눠서 관리?
    public Channel(String channelName, User owner) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;

        if(owner == null) {
            throw new IllegalArgumentException("채널 생성자는 필수");
        }
        if(channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름은 필수");
        }

        this.channelName = channelName;
        this.owner = owner;
    }

    // Getter
    public String getChannelName() {
        return channelName;
    }
    public User getOwner() {
        return owner;
    }

    // update
    public void updateChannelName(String newChannelName) {
        this.channelName = new String(newChannelName);
        this.updateTimestamp();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", ownerId=" + owner.id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}