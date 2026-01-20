package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel createChannel(String channelName) {
        return channelRepository.save(new Channel(channelName));
    }

    @Override
    public List<Channel> getChannelList() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        findUserOrThrow(userId);

        return channelRepository.findAll().stream()
                .filter(channel -> channel.getJoinedUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel getChannelInfoById(UUID channelId) {
        return findChannelOrThrow(channelId);
    }

    @Override
    public Channel updateChannelName(UUID channelId, String newChannelName) {
        Channel channel = findChannelOrThrow(channelId);
        channel.updateChannelName(newChannelName);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelOrThrow(channelId);
        User user = findUserOrThrow(userId);
        // 메모리 갱신
        channel.addUser(user);
        user.updateJoinedChannels(channel);
        // file 갱신
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelOrThrow(channelId);
        User user = findUserOrThrow(userId);

        channel.removeUser(user.getId());
        user.removeChannel(channel);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelOrThrow(channelId);
        channelRepository.deleteById(channelId);
        messageRepository.deleteByChannelId(channelId);
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 사용자가 존재하지 않습니다."));
    }
}
