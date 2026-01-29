package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Channel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // 베이스 코드를 참고하여, 필드 이름 변경: channelName -> name
    private String name;
    private String description;
    private ChannelType type;

    private final List<User> members = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String name, String description, ChannelType type) {
        validateChannel(name);
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;

        if (newName != null && !newName.equals(this.name)) {
            validateChannel(newName);
            this.name = newName;
            anyValueUpdated = true;
        }

        // 설명(description)은 null이거나 비어있을 수도 있으니 동등 비교만 수행
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.update();
        }
    }

    // 채널 참여
    public void addMember(User user) {
        if (user == null) throw new IllegalArgumentException("참여할 유저 정보가 필요합니다.");
        if (members.contains(user)) throw new IllegalArgumentException("이미 채널에 참여 중인 유저입니다.");

        members.add(user);
        user.addChannel(this);
    }

    // 채널 퇴장
    public void removeMember(User user) {
        if (user == null) throw new IllegalArgumentException("퇴장할 유저 정보가 필요합니다.");
        if (!members.contains(user)) throw new IllegalArgumentException("채널에 참여하지 않은 유저는 나갈 수 없습니다.");

        members.remove(user);
        user.removeChannel(this);
    }

    public void addMessage(Message message) {
        if (message != null) messages.add(message);
    }

    public void removeMessage(Message message) {
        if (message != null) messages.remove(message);
    }

    // 채널 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    private void validateChannel(String channelName) {
        // null, Blank 체크
        if (channelName == null || channelName.isEmpty())
            throw new IllegalArgumentException("채널 이름은 필수이며, 비어있을 수 없습니다.");

        // 채널 이름 길이 체크 (2자 이상, 15자 이하)
        if (channelName.length() < 2 || channelName.length() > 15)
            throw new IllegalArgumentException("이름은 2자 이상, 15자 이하로 설정하세요.");
    }

    @Override
    public String toString() {
        return String.format("Channel[이름: %s, Channel ID: %s]", name, getId());
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public List<User> getMembers() {
        return new ArrayList<>(members);
    }
}