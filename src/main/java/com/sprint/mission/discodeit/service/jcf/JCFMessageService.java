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
    private UserService userService;
    private ChannelService channelService;

    public JCFMessageService() {
    }

    // Setter 주입, 순환 참조 문제 회피(생성 시점 딜레이로 문제를 회피)
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String content) {
        if(content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }
        User author = userService.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다. ID: " + authorId));
        Channel channel = channelService.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다. ID: " + channelId));

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
        channelService.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널: " + channelId));

        return messageMap.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 현재는 생성 시간 기준 정렬
                .collect(Collectors.toList());
    }


    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널: " + channelId));

        return channel.getMessages();
    }

    @Override
    public List<Message> findMessagesByAuthor(UUID authorId) {
        User author = userService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저: " + authorId));

        return author.getMessages();
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messageMap.get(messageId));
    }

    @Override
    public void updateMessage(UUID messageId, String newContent) {
        Message message = findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않음: " + messageId));

        if (newContent == null || newContent.trim().isEmpty()) {
            deleteMessage(messageId);
        } else {
            message.updateContent(newContent);
        }
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않음: " + messageId));

        message.getChannel().removeMessage(message);// 채널이 가지고 있는 메시지 리스트에서 삭제
        message.getAuthor().removeMessage(message); // 유저가 가지고 있는 메시지 리스트에서 삭제

        messageMap.remove(messageId); // 전역 메시지 맵에서 삭제
    }

    // 회원 탈퇴, 채널 삭제 등 메시지 전체 삭제
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService
                .findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없음: " + channelId));
        channel.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(this::deleteMessage);
    }

    @Override
    public void deleteMessagesByAuthorId(UUID authorId) {
        User author = userService
                .findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없음: " + authorId));
        author.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(this::deleteMessage);
    }
}