package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap = new LinkedHashMap<>();

    // 연관 도메인의 서비스
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String content) {
        User author = userService.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. ID: " + authorId));
        Channel channel = channelService.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다. ID: " + channelId));

        if (!channel.isAccessibleBy(author)) {
            throw new IllegalArgumentException("이 채널에 메시지를 보낼 권한이 없습니다: " + channel.getChannelName());
        }

        Message message = new Message(content, author, channel);

        // 모든 메시지가 전역적으로 저장됨
        messageMap.put(message.getId(), message);

        // 채널 마다 메시지가 저장됨
        channel.addMessage(message);

        // 유저 마다 메시지가 저장됨
        author.addMessage(message);

        return message;

    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageMap.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 시간순 정렬
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messageMap.get(messageId));
    }

    @Override
    public List<Message> findAll(UUID channelId) {
        Channel channel = channelService.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. 채널 id : " + channelId ));

        return channel.getMessages();

    }

    @Override
    public void updateMessage(UUID messageId, String newContent) {
        findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지가 존재하지 않습니다."))
                .updateContent(newContent);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageMap.get(messageId);

        if (message != null) {
            message.getChannel().getMessages().remove(message);// 채널이 가지고 있는 메시지 리스트에서 삭제
            message.getAuthor().getMessages().remove(message); // 유저가 가지고 있는 메시지 리스트에서 삭제
        }

        messageMap.remove(messageId); // 전역 메시지 맵에서 삭제
    }

    // 회원 탈퇴, 채널 삭제 등 메시지 전체 삭제
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));
        channel.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(messageMap::remove);
        channel.getMessages().clear();
    }

    @Override
    public void deleteMessagesByAuthorId(UUID authorId) {
        User author = userService.findById(authorId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        author.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(messageMap::remove);
        author.getMessages().clear();
    }
}