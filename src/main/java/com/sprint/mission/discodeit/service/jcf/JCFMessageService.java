package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService{
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService){
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    // 메시지 생성
    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateAccess(userId, channelId); // 권한 확인

        User author = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message newMessage = new Message(content,author, channel);

        author.addMessage(newMessage);
        channel.addMessage(newMessage);

        return messageRepository.save(newMessage);
    }

    // 메시지 ID로 조회
    @Override
    public Message findById(UUID id){
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지 ID입니다."));
    }

    // 메시지 전부 조회
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        message.update(content); // 여기서 isEdited가 true로 변함

        return messageRepository.save(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        message.getUser().removeMessage(message);
        message.getChannel().removeMessage(message);

        messageRepository.delete(message);
    }

    // 메시지 고정
    @Override
    public Message togglePin(UUID id){
        Message message = findById(id);
        message.togglePin();

        return messageRepository.save(message);
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId, UUID userId) { // 특정 채널의 메시지 조회
        validateAccess(userId, channelId);
        Channel channel = channelService.findById(channelId);
        return channel.getMessages();
    }

    // 권한 확인
    private void validateAccess(UUID userId, UUID channelId) {
        // 채널 멤버 확인
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!channel.isMember(user)) {
            throw new IllegalArgumentException("채널 멤버만 접근할 수 있습니다.");
        }
    }
}
