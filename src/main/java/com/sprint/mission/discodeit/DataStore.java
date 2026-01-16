package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataStore {
    private final Map<UUID, Channel> channelData = new HashMap<>();
    private final Map<UUID, User> userData = new HashMap<>();
    private final Map<UUID, Message> messageData = new HashMap<>();

    public Map<UUID, Channel> getChannelData() {
        return channelData;
    }

    public Map<UUID, User> getUserData() {
        return userData;
    }

    public Map<UUID, Message> getMessageData() {
        return messageData;
    }

    public void cleanupChannelRelations(Channel channel) {
        // 채널 null 체크
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 채널에 속한 메시지 수집
        List<Message> messages = channel.getMessages();

        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);

        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageData.remove(m.getId()));

        // 채널에 가입된 모든 유저 탈퇴 처리
        channel.getUsers().forEach(u -> u.leaveChannel(channel));
    }

    public void cleanupUserRelations(User user) {
        // 유저 null 체크
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 유저가 작성한 메시지 수집
        List<Message> messages = user.getMessages();

        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);

        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageData.remove(m.getId()));

        // 유저가 가입한 모든 채널에서 탈퇴 처리
        user.getChannels().forEach(ch -> ch.removeUser(user));
    }
}
