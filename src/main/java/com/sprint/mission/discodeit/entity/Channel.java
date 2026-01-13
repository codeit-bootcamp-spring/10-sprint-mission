package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private final UUID channelId; // 채널 식별자
    private String channelName;   // 채널 이름
    private final Long channelCreatedAt; // 채널 생성 시각
    private Long channelUpdatedAt;        // 채널 업데이트 시각
    private final Map<UUID, User> members = new LinkedHashMap<>();
    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    // 채널 생성
    public Channel(String channelName) {
        this.channelId = UUID.randomUUID();
        this.channelName = channelName;
        this.channelCreatedAt = System.currentTimeMillis();
    }

    // 채널 이름 변경 / 시간 갱신
    public void updateChannelName(String channelName){
        this.channelName = channelName;
        this.channelUpdatedAt = System.currentTimeMillis();
    }

    // 채널에 멤버 추가 / 시간 갱신
    public void addMember(User user) {
        members.put(user.getUserId(), user);
        this.channelUpdatedAt = System.currentTimeMillis();
    }

    // 채널에서 멤버 삭제 / 시간 갱신
    public void removeMember(UUID userId) {
        members.remove(userId);
        this.channelUpdatedAt = System.currentTimeMillis();
    }

    // 멤버 이름 리스트 반환
    private List<String> getMemberNames() {
        List<String> names = new ArrayList<>();
        for (User user : members.values()) {
            names.add(user.getUserName());
        }
        return names;
    }

    // 채널 메시지 추가
    public void addMessage(Message message) {
        if (!hasMember(message.getSender().getUserId())) {
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }
        messages.put(message.getMessageId(), message);
        this.channelUpdatedAt = System.currentTimeMillis();
    }

    // 채널 메시지 삭제
    public void removeMessage(UUID messageId) {
        messages.remove(messageId);  // Map에서 실제 삭제
        this.channelUpdatedAt = System.currentTimeMillis();
    }

    // 채널 메시지 조회
    public Collection<Message> getMessages() {
        return messages.values();
    }

    // 외부에서 채널 ID 확인
    public UUID getChannelId() {
        return channelId;
    }

    // 채널 이름 확인
    public String getChannelName() {
        return channelName;
    }

    // 채널 멤버 확인
    public Map<UUID, User> getMembers() {
        return members;
    }

    public boolean hasMember(UUID userId) {
        return members.containsKey(userId);
    }

    // 생성/수정 타임스탬프
    public Long getChannelCreatedAt() {
        return channelCreatedAt;
    }

    public Long getChannelUpdatedAt() {
        return channelUpdatedAt;
    }

    @Override
    public String toString() {
        if (members.isEmpty()) {
            return channelName + "\n(멤버 없음)";
        }
        return channelName + "\n" + String.join(", ", getMemberNames());
    }

    public void printMessages() {
        System.out.println(this.channelName + " 메시지 목록:");
        if (messages.isEmpty()) {
            System.out.println("(메시지 없음)");
        } else {
            for (Message msg : messages.values()) {
                System.out.println(msg);
            }
        }
        System.out.println();
    }
}
