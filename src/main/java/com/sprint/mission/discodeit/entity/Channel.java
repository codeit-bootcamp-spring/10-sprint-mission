package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    // id, createdAt, updatedAt 상속 받음
    private String channelName;
    private User owner;  // 객체 참조로 수정
    // owner는 변경 가능하므로 final 사용 x

    // 생성할 때 무조건 사람이 있는 상태로 시작해야 되는가? -> 아무도 없는 채널을 만드는 건 이상함
    // 생성할 때 생성자 본인은 있어야 함 -> ownderId 받아서 채널 관리자로 설정 필요
    //     그럼 User 객체에 Enum role 선언해서 User별 상태를 따로 관리해줘야 하는가?
    //     채널별로 ADMIN과 USER를 나눠서 관리?

    // BaseEntity() 상속 받음
    public Channel(String channelName, User owner) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        validateChannelName(channelName);
        if(owner == null) {
            throw new IllegalArgumentException("채널(서버) 관리자는 필수");
        }
        this.channelName = channelName;
        this.owner = owner;
    }

    // [] 채널 유효성 검사 규칙 추가
    // 채널 유효성 검사 메서드 분리
    private void validateChannelName(String channelName) {
        if(channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("채널(서버) 이름은 필수");
        }
        // 규칙 추가
    }

    // Getter
    public String getChannelName() {
        return channelName;
    }
    public User getOwner() {
        return owner;
    }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // update
    public void updateChannelName(String newChannelName) {
        this.channelName = new String(newChannelName);
        this.updateTimestamp();
    }
    public void updateOwner(User newOwner) {
        if(owner == null) {
            throw new IllegalArgumentException("새로운 채널(서버) 관리자는 필수");
        }
        if(this.owner.getId().equals(newOwner.getId())) {
            return;
        }
        this.owner = newOwner;
        this.updateTimestamp();
    }
    // updateTimestamp()는 상속받음

//    @Override
//    public String toString() {
//        return "Channel{" +
//                "id=" + id +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//
//                ", channelName='" + channelName +
//                ", owner=" + owner.toString() +
//                '}';
//    }
}