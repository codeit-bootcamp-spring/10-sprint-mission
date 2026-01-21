package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private MessageService messageService;

    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }


    @Override
    public Channel createChannel(String title, String description) {
        validateDuplicateTitle(title);

        Channel channel = new Channel(title, description);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID uuid) {
        return channelRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    @Override
    public Optional<Channel> findChannelByTitle(String title) {
        return findAllChannels().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .findFirst();
    }

    @Override
    public List<Channel> findAllChannels() {
        return this.channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID uuid, String title, String description) {
        Channel channel = getChannel(uuid);
        // title 중복성 검사
        if (title != null && !Objects.equals(channel.getTitle(), title))
            validateDuplicateTitle(title);

        Optional.ofNullable(title).ifPresent(channel::updateTitle);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        channel.updateUpdatedAt();

        return channelRepository.save(channel);
    }

    @Override
    public Channel updateChannel(Channel newChannel) {
        newChannel.updateUpdatedAt();
        return channelRepository.save(newChannel);
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Channel channel = getChannel(uuid);
        deleteProcess(uuid, channel);
    }

    @Override
    public void deleteChannelByTitle(String title) {
        Channel channel = findChannelByTitle(title).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        deleteProcess(channel.getId(), channel);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        if (channel.getParticipants().stream()
                .anyMatch(u -> Objects.equals(u.getId(), user.getId()))) {
            throw new IllegalStateException("이미 참가한 참가자입니다");
        }

        channel.addParticipant(user);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        user.addJoinedChannels(channel);
        userService.updateUser(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        if (channel.getParticipants().stream()
                .noneMatch(u -> Objects.equals(u.getId(), user.getId()))) {
            throw new IllegalStateException("참여하지 않은 참가자입니다");
        }

        channel.removeParticipant(user);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        user.removeJoinedChannels(channel);
        userService.updateUser(user);
    }

    private void validateDuplicateTitle(String title) {
        findChannelByTitle(title).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 채널명입니다"); });
    }

    private void deleteProcess(UUID uuid, Channel channel) {
        List.copyOf(channel.getParticipants()).forEach(u -> leaveChannel(uuid, u.getId()));
        List.copyOf(channel.getMessages()).forEach(m -> messageService.deleteMessage(m.getId()));
        channelRepository.deleteById(uuid);
    }
}
