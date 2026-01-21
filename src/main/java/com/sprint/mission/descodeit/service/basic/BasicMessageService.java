package com.sprint.mission.descodeit.service.basic;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.repository.ChannelRepository;
import com.sprint.mission.descodeit.repository.MessageRepository;
import com.sprint.mission.descodeit.repository.UserRepository;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(UUID userId, String text, UUID channelId) {
        User user = Optional.ofNullable(userRepository.findById(userId))
                .orElseThrow(() -> new NoSuchElementException());
        Channel channel = Optional.ofNullable(channelRepository.findById(channelId))
                .orElseThrow(() -> new NoSuchElementException());
        // 메시지 객체 생성
        Message message = new Message(userId, text, channelId);
        // 유저와 채널에 연관성 추가
        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        // 데이터에 정보 저장
        save(message);
        userRepository.save(userId,user);
        channelRepository.save(channelId,channel);
        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Message message = Optional.ofNullable(messageRepository.findById(messageId))
                .orElseThrow(()-> new NoSuchElementException("해당 메시지를 찾을 수 없습니다"));
        return message;
    }

    @Override
    public List<Message> findAllMessages() {
        List<Message> messageList = messageRepository.findAll();
        System.out.println("[메세지 전체 조회]");
        messageList.forEach(System.out::println);

        return messageList;
    }

    @Override
    public List<Message> findMessageByKeyword(UUID channelId, String keyword) {
        Channel channel = Optional.ofNullable(channelRepository.findById(channelId))
                .orElseThrow(() -> new NoSuchElementException());

        List<Message> messageList = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> message.getText().contains(keyword))
                .toList();
        System.out.println(channel+"채널의 " + "[" + keyword + "]를 포함한 메시지 조회");
        messageList.forEach(System.out::println);

        return messageList;
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        Channel channel = Optional.ofNullable(channelRepository.findById(channelId))
                .orElseThrow(() -> new NoSuchElementException());

        System.out.println("-- " + channel + "에 속한 메시지 조회 --");
        List<Message> messageList = channel.getMessageList().stream()
                .map(messageRepository::findById).filter(Objects::nonNull).toList();

        messageList.forEach(System.out::println);

        return messageList;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findMessage(messageId);

        // 해당 메시지가 속했던 유저와 채널에서 메시지 정보 삭제
        User user  = Optional.ofNullable(userRepository.findById(message.getUserId()))
                .orElseThrow(()-> new NoSuchElementException());
        user.getMessageList().remove(messageId);
        userRepository.save(user.getId(), user);

        // 채널에 속한 메시지 리스트에서 메시지 정보 삭제
        Channel channel = Optional.ofNullable(channelRepository.findById(message.getChannelId()))
                .orElseThrow(() -> new NoSuchElementException());
        channel.getMessageList().remove(messageId);
        channelRepository.save(channel.getId(), channel);

        //데이터에서도 삭제
        messageRepository.delete(messageId);
    }

    @Override
    public Message update(UUID messageId,UUID requestUserId, String newText) {
        Message message = findMessage(messageId);
        User user  = Optional.ofNullable(userRepository.findById(requestUserId))
                .orElseThrow(()-> new NoSuchElementException());

        // 권한 체크
        if(!requestUserId.equals(message.getUserId())){
            throw new IllegalStateException("수정할 권한이 없습니다");
        }
        // 메시지 업데이트
        message.updateMessage(newText);
        save(message);

        return message;
    }

    @Override
    public void save(Message message) {
        messageRepository.save(message.getId(),message);
    }
}
