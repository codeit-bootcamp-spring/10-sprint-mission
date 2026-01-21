package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    // 필드
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    private MessageService messageService;
    private ChannelService channelService;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        return userRepository.save(user);
    }

    @Override
    public User find(UUID id) {
        return userRepository.find(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateName(UUID userID, String name) {
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        user.updateName(name);

        Set<UUID> channelIDs = new HashSet<>();

        for (Channel channel : user.getChannels()) {
            for (User u : channel.getMembersList()){
                if (u.getId().equals(userID)) {
                    u.updateName(name);
                    channelIDs.add(channel.getId());
                }
            }
        }

        for (UUID channelID : channelIDs) {
            channelService.updateName(channelID, name);
        }

        Set<UUID> messageIDs = new HashSet<>();
        for (Message message : user.getMessageList()) {
            message.getSender().updateName(name);
            messageIDs.add(message.getId());
        }

        for (UUID messageID : messageIDs) {
            messageRepository.save(messageRepository.find(messageID).get());
        }
        // [저장] 변경사항 저장
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userID) {
        if (messageService == null) {
            throw new IllegalStateException("MessageService is not set in BasicUserService");
        }

        // [저장]
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // [비즈니스] : 진짜 유저가 보낸 메시지만 삭제하도록
        List<Message> messages = new ArrayList<>(user.getMessageList());
        for (Message message : messages) {
            if (message.getSender() != null && message.getSender().getId().equals(userID)) {
                messageService.deleteMessage(message.getId());
            }
        }


        // [비즈니스] ??
        List<UUID> channelIDs = new ArrayList<>();
        for (Channel channel : user.getChannels()) {
            channelIDs.add(channel.getId());
        }

        for (UUID channelID : channelIDs) {
            channelService.leaveChannel(userID, channelID);
        }

        // [저장]
        userRepository.deleteUser(user);
    }

    @Override
    public List<String> findJoinedChannels(UUID userID) {
        User user = find(userID);
        return user.getChannels().stream()
                .map(Channel::getName)
                .collect(java.util.stream.Collectors.toList());
    }
}
