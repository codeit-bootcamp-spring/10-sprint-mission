package com.sprint.mission.discodeit.consistency;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class JCFConsistencyManager {
    private final MessageRepository messageRepository;

    public JCFConsistencyManager(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void cleanupChannelRelations(Channel channel) {
        // 채널 null 체크
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 채널에 속한 메시지 수집
        List<Message> messages = new ArrayList<>(channel.getMessages());

        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);

        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageRepository.deleteMessage(m.getId()));

        // 채널에 가입된 모든 유저 탈퇴 처리
        channel.getUsers().forEach(u -> u.leaveChannel(channel));
    }

    public void cleanupUserRelations(User user) {
        // 유저 null 체크
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 유저가 작성한 메시지 수집
        List<Message> messages = new ArrayList<>(user.getMessages());

        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);

        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageRepository.deleteMessage(m.getId()));

        // 유저가 가입한 모든 채널에서 탈퇴 처리
        user.getChannels().forEach(ch -> ch.removeUser(user));
    }
}
