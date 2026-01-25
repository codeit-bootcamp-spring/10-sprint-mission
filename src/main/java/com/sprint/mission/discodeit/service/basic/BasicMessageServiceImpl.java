package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BasicChannelService;
import com.sprint.mission.discodeit.service.BasicUserService;
import com.sprint.mission.discodeit.util.Validator;


import java.util.List;
import java.util.UUID;

public class BasicMessageServiceImpl implements com.sprint.mission.discodeit.service.BasicMessageService {
    private final MessageRepository messageRepository;
    private BasicUserService userService;
    private BasicChannelService channelService;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;

    public BasicMessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    public Message createMessage(UUID userId, String content, UUID channelId) {
        Validator.validateNotNull(userId, "메시지 생성 시 userId가 null일 수 없음");
        Validator.validateNotNull(content, "메시지 생성 시 content가 null일 수 없음");
        Validator.validateNotNull(channelId, "메시지 생성 시 channelId가 null일 수 없음");
        Validator.validateNotBlank(content,"메시지 생성 시 content가 빈문자열일 수 없음");
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!user.isInChannel(channel)) {
            throw new IllegalStateException("해당 채널에 참여 중이 아니므로 메시지를 작성할 수 없음");
        }
        Message message = new Message(user, content, channel);
        user.addMessage(message, channel);
        channel.addMessage(message);

        userRepository.save(user);
        channelRepository.save(channel);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 id의 메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateById(UUID id, String content) {
        Message message = findById(id);
        Validator.validateNotNull(content, "업데이트하려는 메시지 내용이 null일 수 없음");
        Validator.validateNotBlank(content, "업데이트하려는 메시지 내용이 빈내용일 수 없음");
        message.updateContent(content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void deleteById(UUID id) {
        Message message = findById(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.removeMessage(message);
        channelRepository.save(channel);

        user.removeMessage(message, channel);
        userRepository.save(user);
        channelRepository.save(channel);

        messageRepository.deleteById(id);
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        User user = userService.findById(userId);
        return messageRepository.findAll().stream()
                .filter(message -> message.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannel().equals(channel))
                .toList();
    }

    @Override
    public void setUserService(BasicUserService userService) {
        this.userService = userService;
    }

    @Override
    public void setChannelService(BasicChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
}
