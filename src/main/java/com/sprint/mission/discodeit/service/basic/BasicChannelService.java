package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        Validators.validationChannel(type, channelName, channelDescription);
        Channel channel = new Channel(type, channelName, channelDescription);

        return channelRepository.save(channel);
    }

    @Override
    public Channel readChannel(UUID id) {
        return validateExistenceChannel(id);
    }

    @Override
    public List<Channel> readAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Channel channel = validateExistenceChannel(id);

        Optional.ofNullable(type).ifPresent(t -> {Validators.requireNonNull(t, "type");
            channel.updateChannelType(t);
        });
        Optional.ofNullable(channelName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "channelName");
                    channel.updateChannelName(name);
                });
        Optional.ofNullable(channelDescription).ifPresent(des -> {
            Validators.requireNotBlank(des, "channelDescription");
            channel.updateChannelDescription(des);
        });

        channelRepository.save(channel);
        return channel;
    }



    @Override
    public void deleteChannel(UUID id) {
        validateExistenceChannel(id);
        channelRepository.deleteById(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        boolean alreadyJoined = channel.getJoinedUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().add(user);
        user.getJoinedChannelIds().add(channel);

        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        boolean alreadyLeaved = channel.getJoinedUsers().stream()
                .noneMatch(u -> u.getId().equals(userId));

        if (alreadyLeaved) {
            throw new IllegalArgumentException("채널에 속해 있지 않은 유저입니다.");
        }

        channel.getJoinedUsers().removeIf(u -> u.getId().equals(userId));
        user.getJoinedChannels().removeIf(c -> c.getId().equals(channelId));
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public List<Channel> readChannelsByUser(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getJoinedUsers().stream()
                        .anyMatch(u -> userId.equals(u.getId())))
                .toList();
    }


    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }
}