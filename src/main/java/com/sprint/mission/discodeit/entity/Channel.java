package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Entity {
    private String name;
    private User owner;
    private final List<User> users;

    public Channel(String name, User owner) {
        super();
        if (owner == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>();
        this.users.add(owner);
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        // 유저 null 체크
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 가입 여부 확인, 이미 존재하는 유저라면 예외 발생
        if (users.contains(user)) {
            throw new RuntimeException("이미 채널에 가입한 유저입니다.");
        }

        // 유저 추가
        users.add(user);
        // 수정 시각 갱신
        super.update();
    }

    public void removeUser(User user) {
        // 유저 null 체크
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        if (user.equals(owner)) {
            throw new RuntimeException("채널 소유자는 탈퇴할 수 없습니다.");
        }

        // 존재하는 유저인 경우에만 제거 및 수정 시각 갱신
        boolean removed = users.remove(user);
        if (!removed) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        super.update();
    }

    public Channel updateChannelName(String name) {
        // 채널 이름 변경
        this.name = name;
        // 수정 시각 갱신
        super.update();
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel [id=%s, name=%s, owner=%s]",
                getId().toString().substring(0, 5),
                name,
                owner.getId().toString().substring(0, 5)
        );
    }
}
