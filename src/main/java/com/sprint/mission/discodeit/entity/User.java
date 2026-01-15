package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity {
    private final String email;
    private String nickname;
    private final List<Channel> channels;
    private final List<Message> messages;

    public User(String nickname, String email) {
        super();
        this.nickname = nickname;
        this.email = email;
        this.channels = new ArrayList<>(); // 순서를 기억하고 보편적인 컬렉션을 사용하기 위해 선택
        this.messages = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public List<Channel> getChannels() {
        return new ArrayList<>(channels);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void joinChannel(Channel channel) {
        // 가입 여부 확인, 이미 가입한 채널이라면 예외 발생
        if (channels.contains(channel)) {
            throw new RuntimeException("이미 가입한 채널입니다.");
        }

        // 가입 채널 추가
        channels.add(channel);

        // 수정 시각 갱신
        super.update();
    }

    public void leaveChannel(Channel channel) {
        // 채널 null 체크
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 가입한 채널인 경우에만 제거 및 수정 시각 갱신
        boolean removed = channels.remove(channel);
        if (removed) {
            super.update();
        }
    }

    public void addMessage(Message message) {
        // 메시지 null 체크
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 작성자 일치 여부 확인
        if (!message.getUser().equals(this)) {
            throw new RuntimeException("메시지 작성자가 일치하지 않습니다.");
        }

        // 메시지 추가
        messages.add(message);
        // 수정 시각 갱신
        super.update();
    }

    public void removeMessage(Message message) {
        // 메시지 null 체크, 채널 및 유저 검증은 MessageService에서 진행
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 존재하는 메시지인 경우에만 제거 및 수정 시각 갱신
        boolean removed = messages.remove(message);
        if (!removed) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        super.update();
    }

    public User updateUserNickname(String nickname) {
        // 닉네임 변경
        this.nickname = nickname;
        // 수정 시각 갱신
        super.update();
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "User [id=%s, nickname=%s, email=%s, joinedChannels=%s, messageCount=%s]",
                getId().toString().substring(0, 5),
                nickname,
                email,
                channels.stream()
                        .map(ch -> "[id=" + ch.getId().toString().substring(0,5) + ", name=" + ch.getName() + "]").toList(),
                messages.size()
        );
    }
}
