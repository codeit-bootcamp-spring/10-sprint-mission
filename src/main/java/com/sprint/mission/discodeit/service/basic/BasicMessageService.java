package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private UserService userService;
    private ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String content) {
        // 1. 비즈니스 로직: 객체 로드 및 생성
        User author = userService.findById(authorId);
        Channel channel = channelService.findById(channelId);
        Message message = new Message(content, author, channel);

        // 2. 저장 로직 위임
        messageRepository.save(message);

        // 3. 비즈니스 로직: 양방향 관계 동기화
        author.addMessage(message);
        userService.save(author);

        channel.addMessage(message);
        channelService.save(channel);

        return message;
    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        // 방법 1: 채널 서비스에서 가져오기 (비즈니스적 접근)
        // return channelService.findById(channelId).getMessages();

        // 방법 2: 레포지토리에서 전체 조회 후 필터링 (저장소 중심 접근)
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findMessagesByAuthor(UUID authorId) {
        // 방법 1: 유저 서비스에서 가져오기 (비즈니스적 접근)
        //return userService.findById(authorId).getMessages();

        // 방법 2: 레포지토리에서 전체 조회 후 필터링 (저장소 중심 접근)
        return messageRepository.findAll().stream()
                .filter(m -> m.getAuthor().getId().equals(authorId))
                .collect(Collectors.toList());
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다. " + messageId));
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다: " + messageId));

        // 비즈니스 로직: 내용이 비어있으면 삭제, 아니면 수정
        if (newContent == null || newContent.isBlank()) {
            deleteMessage(messageId);
            return message;
        }

        message.updateContent(newContent);
        messageRepository.save(message);

        // 비즈니스 로직: 작성자 및 채널 내 리스트 동기화
        User author = userService.findById(message.getAuthor().getId());
        author.updateMessageInList(message);
        userService.save(author);

        Channel channel = channelService.findById(message.getChannel().getId());
        channel.updateMessageInList(message);
        channelService.save(channel);

        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다: " + messageId));

        // 비즈니스 로직: 관계 제거
        message.getChannel().removeMessage(message);
        channelService.save(message.getChannel());

        message.getAuthor().removeMessage(message);
        userService.save(message.getAuthor());

        // 저장 로직 위임
        messageRepository.deleteById(messageId);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        List<Message> targets = findMessagesByChannel(channelId);
        targets.forEach(m -> messageRepository.deleteById(m.getId()));
    }

    @Override
    public void deleteMessagesByAuthorId(UUID authorId) {
        List<Message> targets = findMessagesByAuthor(authorId);
        targets.forEach(m -> messageRepository.deleteById(m.getId()));
    }
}