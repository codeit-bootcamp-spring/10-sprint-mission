package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
# 리펙토링 포인트 1
Channel에 owner를 넣게되면 Channel과 owner의 결합도 높아진다.
-> 정규화를 통해 ChannelMember(관계 테이블)를 통해 완전히 분리하는 방법 고려
(장) ChannerlMember는 채널과 유저 사이의 관계만 관리 -> Service 계층에서 연결 구현
(단) 복잡도 높아짐. Join 연산 필요(-> Index로 성능 극복?)
=> 어떤 것이 더 맞는 설계인가? / DB 없는 설계에서 고민해야 될 내용인가?
 */

public class Channel extends BaseEntity {

    // === 2 필드 ===
    // id, createdAt, updatedAt 상속 받음
    private String channelName;
    private User owner ;  // 객체 참조로 수정
    // owner는 변경 가능하므로 final 사용 x

    // 1. 채널에 작성된 메시지 목록
    private List<Message> messages = new ArrayList<>();
    // 2. 채널에 참여 중인 유저(Role 포함) 목록
    private List<ChannelUserRole> channelUserRoles = new ArrayList<>();

    // 생성할 때 무조건 사람이 있는 상태로 시작해야 되는가? -> 아무도 없는 채널을 만드는 건 이상함
    // 생성할 때 생성자 본인은 있어야 함 -> ownderId 받아서 채널 관리자로 설정 필요
    //     그럼 User 객체에 Enum role 선언해서 User별 상태를 따로 관리해줘야 하는가?
    //     채널별로 ADMIN과 USER를 나눠서 관리?

    // === 3 생성자 ===
    // BaseEntity() 상속 받음
    public Channel(String channelName, User ownerUser) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        validateChannelName(channelName);
        validateOwnerId(ownerUser.getId());

        this.channelName = channelName;
        this.owner = ownerUser;
    }

    // === 4 공개 메서드 ===
    // Getter
    public String getChannelName() {
        return channelName;
    }
    public User getOwner() {
        return owner;
    }
    public List<Message> getMessages() { return messages; }

    public List<ChannelUserRole> getChannelUserRoles() { return channelUserRoles; }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // update
    public void updateChannelName(String newChannelName) {
        this.channelName = newChannelName;
        this.updateTimestamp();
    }
    public void updateOwner(User newOwner) {
        validateOwnerId(newOwner.getId());
        if(this.owner.getId().equals(newOwner.getId())) {
            return;
        }
        this.owner = newOwner;
        this.updateTimestamp();
    }
    // 채널에 메시지 추가
    public void addMessage(Message message) {
        this.messages.add(message);
    }
    // 채널에서 메시지 제거
    public void removeMessage(Message message) {
        this.messages.remove(message);
    }
    // 채널에 사용자(Role) 추가
    public void addChannelUserRole(ChannelUserRole role) {
        this.channelUserRoles.add(role);
    }
    // 채널에서 사용자(Role) 제거

    public void removeChannelUserRole(ChannelUserRole role) {
        this.channelUserRoles.remove(role);
    }

    // === 5 비공개 메서드 ===
    // [] 채널 유효성 검사 규칙 추가
    // 채널 유효성 검사 메서드 분리
    private void validateChannelName(String channelName) {
        if(channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("채널(서버) 이름은 필수");
        }
        // 규칙 추가
    }

    private void validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("채널 관리자(Owner ID)는 필수");
        }
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