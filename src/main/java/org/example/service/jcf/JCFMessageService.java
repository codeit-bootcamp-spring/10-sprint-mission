package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.exception.InvalidRequestException;
import org.example.exception.NotFoundException;
import org.example.service.ChannelService;
import org.example.service.MessageService;
import org.example.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID senderId, UUID channelId) {

        if (content == null || content.isBlank()) {
            throw new InvalidRequestException("content", "null이 아니고 빈 값이 아님", content);
        }

        User sender = userService.findById(senderId);
        Channel channel = channelService.findById(channelId);
        Message message = new Message(content,sender,channel);
        data.put(message.getId(),message);
//        channel.getMessages().add(message);
//        sender.getMessages().add(message);
        message.addToChannelAndUser();

        return message;
    }

    @Override
    public Message findById(UUID messageId){
        return Optional.ofNullable(data.get(messageId))
                .orElseThrow(()->new NotFoundException("id", "존재하는 메시지", messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {

        // 채널 존재 여부 검증
        channelService.findById(channelId);

        return data.values().stream()
                .filter(message -> !message.isDeletedAt())  // 삭제상태 메시지 제외
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySender(UUID senderId) {

        // 유저 존재 여부 검증
        userService.findById(senderId);

        return data.values().stream()
                .filter(message -> !message.isDeletedAt())  // 삭제된 상태 메시지 제외
                .filter(message -> message.getSender().getId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);

        message.updateContent(content);
        message.updateEditedAt(true);
        return message;
    }

    @Override
    public void softDelete(UUID messageId) {
        Message message = findById(messageId);
        message.updateDeletedAt(true);
    }

    @Override
    public void hardDelete(UUID messageId) {
        Message message = findById(messageId);
//        message.getChannel().getMessages().remove(message); // 채널의 메시지 리스트에서 제거 (양방향 관계 정리)
//        message.getSender().getMessages().remove(message);
        message.removeFromChannelAndUser();
        data.remove(messageId);
    }

}
