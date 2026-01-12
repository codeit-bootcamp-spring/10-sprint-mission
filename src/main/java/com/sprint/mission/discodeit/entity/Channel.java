package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String channelName;

    public Channel(String channelName) {
        validateChannel(channelName);
        this.channelName = channelName;
    }

    public void updateChannel(String channelName) {
        validateChannel(channelName);
        this.channelName = channelName;
        super.update();
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
}