package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    // 1. 필드 (상태)
    private String username;
    private final List<Message> messages = new ArrayList<>();
    private final List<Channel> channels = new ArrayList<>();

    // 2. 생성자
    public User(String username) {
        super(); // 부모 생성자 호출 (명시적 표시)
        this.username = username;
    }

    // 3. 비즈니스 로직 (행위/수정)
    // "단순 setter가 아니라 의미 있는 행위들을 위로 올립니다"
    public void updateUsername(String username) {
        this.username = username;
        this.updateUpdatedAt(); // BaseEntity의 메서드를 활용해 수정 시간 갱신
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }

    // 4. Getter (단순 조회)
    // "로직이 없는 단순 조회는 아래로 모아서 시야를 방해하지 않게 합니다"
    public String getUsername() { return username; }
    public List<Message> getMessages() { return messages; }
    public List<Channel> getChannels() { return channels; }

    // 5. 공통 메서드 (재정의)
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
