package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BasicUserService;
import com.sprint.mission.discodeit.service.BasicMessageService;
import com.sprint.mission.discodeit.util.Validator;

import java.util.List;
import java.util.UUID;

public class BasicChannelServiceImpl implements com.sprint.mission.discodeit.service.BasicChannelService {
    private final ChannelRepository channelRepository;
    private BasicUserService userService;
    private BasicMessageService messageService;
    private UserRepository userRepository;
    private MessageRepository messageRepository;


    public BasicChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
    @Override
    public Channel createChannel(String channelName) {
        Validator.validateNotNull(channelName, "생성하고자하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(channelName, "생성하고자하는 채널의 채널명이 빈문자열일 수 없음");
        Channel channel = new Channel(channelName.trim());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 id의 채널을 찾을 수 없음"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateById(UUID id, String channelName) {
        Channel channel = findById(id);
        Validator.validateNotNull(channelName, "업데이트하고자 하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(channelName, "업데이트하고자 하는 채널의 채널명이 빈 문자열일 수 없음");
        channel.setChannelName(channelName.trim());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        Channel channel = findById(id);
        //채널에 참여 중인 유저 리스트
        List<User> users = userService.getUsersByChannelId(channel.getId());
        // 채널에 남겨진 메시지 리스트
        List<Message> messages = messageService.getMessagesByChannelId(channel.getId());

        // 유저 객체의 채널 리스트에서 채널 삭제
        for (User user : users) {
            user.leaveChannel(channel);
            // 유저가 갖고 있는 채널 리스트에서 삭제
            // 해당 채널의 참여 중인 유저리스트에서 유저 삭제
            channelRepository.save(channel);
            userRepository.save(user);
        }
        
        // 메시지 삭제
        for (Message message : messages) {
            messageService.deleteById(message.getId());
            message.getUser().removeMessage(message, channel); // 유저의 메시지 목록에서 삭제 + 해당 채널의 메시지 목록에서 삭제
            userRepository.save(message.getUser());
            channelRepository.save(channel);
        }
        channelRepository.deleteById(id);
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID id) {
        return channelRepository.findAll()
                .stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(id)))
                .toList();
    }


    @Override
    public void setMessageService(BasicMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void setUserService(BasicUserService userService) {
        this.userService = userService;
    }

    @Override
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
