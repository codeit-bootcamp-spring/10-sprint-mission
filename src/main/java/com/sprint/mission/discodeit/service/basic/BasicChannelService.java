package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService, ClearMemory {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public Channel create(String name, IsPrivate isPrivate, UUID ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel = new Channel(name, isPrivate, user);
        channelRepository.save(channel);
        userStatusRepository.findByUserId(ownerId)
                .ifPresent(UserStatus::updateLastActiveTime);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다."));

        return channel;
    }

    @Override
    public List<Channel> readAll() {
        return channelRepository.readAll();
    }

    @Override
    public Channel update(UUID id, String name, IsPrivate isPrivate, User owner) {
        Channel channel = findById(id);
        channel.updateName(name);
        channel.updatePrivate(isPrivate);
        channel.updateOwner(owner);
        return channelRepository.save(channel);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = findById(channelId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        channel.addUser(user);
        channelRepository.save(channel);
        userRepository.save(user);
        userStatusRepository.findByUserId(userId).orElse(null).updateLastActiveTime();

    }

    @Override
    public List<Message> getChannelMessages(UUID channelId) {
        findById(channelId);
        return messageRepository.readAll().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<User> getChannelUsers(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getUsers();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        // 채널의 메시지 삭제하기
        List<Message> messages = channel.getMessages();
        messages.forEach(m ->messageRepository.delete(m.getId()));

        channelRepository.delete(id);
    }

    @Override
    public void clear() {
        channelRepository.clear();
    }

}
