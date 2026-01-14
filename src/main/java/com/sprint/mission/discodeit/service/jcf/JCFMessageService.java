package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    // 전체 메세지 저장
    private final Map<UUID, Message> repository = new HashMap<>();
    // 채널 JCF 객체 저장
    private final ChannelService channelService;

    public JCFMessageService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, User user, Channel channel) {    // 내용, 사용자, 채널 객체 받아서 메세지 생성

        Objects.requireNonNull(content, "메시지 내용은 null일 수 없습니다.");
        Objects.requireNonNull(user, "메시지 작성자(User)는 null일 수 없습니다.");
        Objects.requireNonNull(channel, "메시지 대상 채널(Channel)은 null일 수 없습니다.");

        if (!channel.getUsers().contains(user))
            throw new IllegalArgumentException("해당 채널의 멤버가 아니면 메시지를 작성할 수 없습니다.");
        Message message = new Message(content, user, channel);
        repository.put(message.getId(), message);
        channel.addMessage(message);

        return message;
    }


    @Override
    public Message findById(UUID messageId) {
        Objects.requireNonNull(messageId, "조회하려는 메시지 ID가 null입니다.");
        return repository.get(messageId);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "조회하려는 채널 ID가 null입니다.");
        // 해당 채널 ID를 가진 메시지만 필터링하여 리스트로 반환
        Channel channel = channelService.findById(channelId);
        if (channel == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return List.of(); // null 대신 빈 리스트 반환이 안전합니다.
        }

        return channel.getMessages();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "수정하려는 메시지 ID가 null입니다.");
        Objects.requireNonNull(content, "수정할 내용이 null입니다.");

        Message message = repository.get(messageId);
        if (message == null) {
            System.out.println("메세지가 존재하지 않습니다.");
            return null;
        }

        message.update(content);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Objects.requireNonNull(messageId, "메세지 Id가 유효하지 않습니다.");

        Message message = repository.remove(messageId);
        if (message == null) {
            System.out.println("삭제하려는 메세지가 존재하지 않습니다.");
            return;
        }
        Channel channel = channelService.findById(message.getChannelId());
        if (channel != null) {
            channel.removeMessage(messageId);

        }
    }
}
