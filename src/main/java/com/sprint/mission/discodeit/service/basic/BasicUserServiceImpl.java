package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BasicChannelService;
import com.sprint.mission.discodeit.service.BasicMessageService;
import com.sprint.mission.discodeit.util.Validator;

import java.util.List;
import java.util.UUID;

public class BasicUserServiceImpl implements com.sprint.mission.discodeit.service.BasicUserService {
    private final UserRepository userRepository;
    private BasicChannelService channelService;
    private BasicMessageService messageService;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    public BasicUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName) {
        Validator.validateNotNull(userName, "생성하고자 하는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(userName, "생성하고자 하는 유저의 이름이 빈문자열일 수 없음");
        User user = new User(userName.trim());
        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 id의 사용자를 찾을 수 없음"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateById(UUID id, String userName) {
        User user = findById(id);
        Validator.validateNotNull(userName, "변경 하려는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(userName, "변경 하려는 유저의 이름이 빈문자열일 수 없음");
        user.setUserName(userName.trim());
        userRepository.save(user);
        return user;
    }


    @Override
    public void deleteById(UUID id) {
        User user = findById(id);
        // 유저가 참여중인 channel 리스트
        List<Channel> channels = channelService.getChannelsByUserId(id);
        // 유저가 작성했던 message 리스트
        List<Message> messages = messageService.getMessagesByUserId(id);

        // 참여중인 채널의 유저리스트에서 유저를 제거
        for (Channel channel : channels) {
            channel.removeJoinedUser(user);
            // 채널의 참여중인 리스트에서 유저 제거 + 유저의 참여중인 채널 리스트에서 채널 제거
            channelRepository.save(channel);
            userRepository.save(user);
        }
        // File 레포지토리를 사용하면 여기서 channel관련해서 저장이 되야하지 않나??
        // 그러면 channel.removeJoinedUser(user)를 호출하는게 아니라
        // 채널 레포를 가진 채널 서비스가 이런 기능을 정의하고 여기서 사용해야하나?
        // 채널 서비스는 이런 기능 안에 채널 레포에 대해 save, load를 할테니까
        // 아니면 channelRepository를 필드로 갖고 .save(channel)을 해야하나?


        // 작성했던 메시지가 포함된 채널의 메시지 리스트에서 메시지를 제거
        for (Message message : messages) {
            message.getChannel().removeMessage(message);
            channelRepository.save(message.getChannel());

            messageService.deleteById(message.getId());
//            messageRepository.save(message); >> 이걸 지워야 JCF에서 동작

        }
        // 여기서 또 message.getChannel().removeMessage(message)를 하면
        // 채널도 저장이 되어야하는데 어떻게?

        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        return userRepository.findAll()
                .stream()
                .filter(user ->
                        user.getChannels()
                                .stream()
                                .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.joinChannel(channel);
        // 이러면 File 레포지토리를 사용할 때
        // 유저 데이터랑 채널 데이터 저장이 안되지 않나?
        // 그냥 userRepository.save(user), channelRepository.save(channel)??
        // >> 그럼 유저서비스가 channel repo를 필드로???
        userRepository.save(user);
        channelRepository.save(channel);
    }

    public void setChannelService(BasicChannelService channelService) {
        this.channelService = channelService;
    }
    public void setMessageService(BasicMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
