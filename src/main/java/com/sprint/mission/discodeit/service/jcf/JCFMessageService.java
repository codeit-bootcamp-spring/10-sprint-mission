package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data = new LinkedHashMap<>();

    // 연관 도메인의 서비스
    private UserService userService;
    private ChannelService channelService;

    // Setter 주입, 순환 참조 문제 회피(생성 시점 딜레이로 문제를 회피)
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String content) {
        //엔티티에서 빈 메시지 검증함

        User author = userService.findById(authorId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(content, author, channel);

        data.put(message.getId(), message); // 모든 메시지가 전역적으로 저장됨
        channel.addMessage(message); // 채널 마다 메시지가 저장됨
        author.addMessage(message); // 유저 마다 메시지가 저장됨

        return message;

    }

//    @Override
//    public List<Message> findAllByChannelId(UUID channelId) { // 전체 메시지 풀에서 채널 ID에 해당하는 메시지를 찾음
//        channelService.findById(channelId);
//
//        return messageMap.values().stream()
//                .filter(m -> m.getChannel().getId().equals(channelId))
//                .sorted(Comparator.comparing(Message::getSequence)) // 메시지 순서 보장을 위해선 sequence 정렬
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Message> findAllByUserId(UUID userId) { // 전체 메시지 풀에서 유저 ID에 해당하는 메시지를 찾음
//        userService.findById(userId);
//
//        return messageMap.values().stream()
//                .filter(message -> message.getAuthor().getId().equals(userId))
//                .sorted(Comparator.comparing(Message::getSequence)) // 메시지 순서 보장을 위해선 sequence 정렬
//                .collect(Collectors.toList());
//    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) { // 채널이 가지고 있는 메시지만 가져옴
        Channel channel = channelService.findById(channelId);

        return channel.getMessages();
    }

    @Override
    public List<Message> findMessagesByAuthor(UUID authorId) { // 유저가 가지고 있는 메시지만 가져옴
        User author = userService.findById(authorId);

        return author.getMessages();
    }

    @Override
    public Message findById(UUID messageId) {
        Message message = data.get(messageId);

        if (message == null) {
            throw new NoSuchElementException("메시지를 찾을 수 없습니다.: " + messageId);
        }

        return message;
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findById(messageId);

        if (newContent == null || newContent.isBlank()) {
            deleteMessage(messageId); // 수정할 땐 빈 메시지 보낼 시 삭제됨.
        } else {
            message.updateContent(newContent);
        }

        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findById(messageId);

        message.getChannel().removeMessage(message);// 채널이 가지고 있는 메시지 리스트에서 삭제
        message.getAuthor().removeMessage(message); // 유저가 가지고 있는 메시지 리스트에서 삭제

        data.remove(messageId); // 전역 메시지 맵에서 삭제, 만약 이 부분을 제거하면 소프트 delete가 된다.
    }

    // 회원 탈퇴, 채널 삭제 등 메시지 전체 삭제
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService
                .findById(channelId);
        channel.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(this::deleteMessage);
    }

    @Override
    public void deleteMessagesByAuthorId(UUID authorId) {
        User author = userService
                .findById(authorId);
        author.getMessages()
                .stream()
                .map(Message::getId)
                .forEach(this::deleteMessage);
    }

}