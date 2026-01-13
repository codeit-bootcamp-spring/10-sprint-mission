package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private final Set<UUID> memberIds = new HashSet<>();

    public Channel(String channelName) {
        validateChannel(channelName);
        this.channelName = channelName;
    }

    public void updateChannel(String channelName) {
        validateChannel(channelName);
        this.channelName = channelName;
        super.update();
    }

    // 채널 참여
    public void join(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("참여할 유저 ID가 필요합니다.");
        if (memberIds.contains(userId)) throw new IllegalArgumentException("이미 채널에 참여 중인 유저입니다.");
        memberIds.add(userId);
    }

    // 채널 퇴장
    public void leave(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("퇴장할 유저 ID가 필요합니다.");
        if (memberIds.contains(userId)) throw new IllegalArgumentException("채널에 참여하지 않은 유저는 나갈 수 없습니다.");
        memberIds.remove(userId);
    }

    // 채널 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    private void validateChannel(String channelName) {
        // null, Blank 체크
        if (channelName == null || channelName.isEmpty()) throw new IllegalArgumentException("채널 이름은 필수이며, 비어있을 수 없습니다.");

        // 채널 이름 길이 체크 (2자 이상, 15자 이하)
        if (channelName.length() < 2 || channelName.length() > 15) throw new IllegalArgumentException("이름은 2자 이상, 15자 이하로 설정하세요.");
    }

    @Override
    public String toString() {
        return String.format("Channel[이름: %s, Channel ID: %s]", channelName, getId());
    }

    public String getChannelName() {
        return channelName;
    }

    public Set<UUID> getMemberIds() {
        return new HashSet<>(memberIds);
    }
}