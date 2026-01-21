package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository,
                               UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                                           .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다"));
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다"));

        if (!channel.getParticipants().contains(user)) {
            throw new IllegalArgumentException("채널에 참여하지 않은 사용자는 메시지를 작성할 수 없습니다.");
        }

        Message newMessage = new Message(content, channel, user);

        user.addMessage(newMessage);
        channel.addMessage(newMessage);

        messageRepository.save(newMessage);
        channelRepository.save(channel);
        userRepository.save(user);

        return newMessage;
    }

    @Override
    public void deleteMessage(UUID id) {
        Message targetMessage = findMessageById(id);
        User user = userRepository.findById(targetMessage.getUser().getId())
                                  .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다"));
        Channel channel = channelRepository.findById(targetMessage.getChannel().getId())
                                           .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다"));


        user.getMyMessages().remove(targetMessage);
        userRepository.save(user);

        channel.getChannelMessages().remove(targetMessage);
        channelRepository.save(channel);


        messageRepository.delete(id);
        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());
    }

    @Override
    public Message findMessageById(UUID id) {
        return messageRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 없습니다."));
    }

    @Override
    public Message updateMessage(UUID id, String newContent) {
        Message targetMessage = findMessageById(id);
        targetMessage.updateContent(newContent);
        messageRepository.save(targetMessage);

        User author = userRepository.findById(targetMessage.getUser().getId())
                                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다"));;
        // 작성자의 메시지 리스트에서 해당 메시지를 찾아 최신 내용으로 교체
        author.getMyMessages().replaceAll(m -> m.equals(targetMessage) ? targetMessage : m);
        userRepository.save(author);


        // 소속 채널의 메시지 목록 업데이트
        Channel channel = channelRepository.findById(targetMessage.getChannel().getId())
                                           .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다"));;

        // 채널의 메시지 리스트에서 해당 메시지를 찾아 최신 내용으로 교체
        channel.getChannelMessages().replaceAll(m -> m.equals(targetMessage) ? targetMessage : m);
        channelRepository.save(channel);


        System.out.println("메시지가 수정되었습니다");
        return targetMessage;
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageRepository.findByUserId(userId);
    }
}
