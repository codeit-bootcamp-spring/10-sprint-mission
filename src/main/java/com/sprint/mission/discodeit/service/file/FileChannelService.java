package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final FileUserRepository fileUserRepository;
    private final FileChannelRepository fileChannelRepository;

    public FileChannelService(FileUserRepository fileUserRepository, FileChannelRepository fileChannelRepository) {
        this.fileUserRepository = fileUserRepository;
        this.fileChannelRepository = fileChannelRepository;
    }

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        Validators.validationChannel(type, channelName, channelDescription);
        Channel channel = new Channel(type, channelName, channelDescription);

        return fileChannelRepository.save(channel);
    }

    @Override
    public Channel readChannel(UUID id) {
        return validateExistenceChannel(id);
    }

    @Override
    public List<Channel> readAllChannel() {
        return fileChannelRepository.findAll();
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

        fileChannelRepository.save(channel);
        return channel;
    }



    @Override
    public void deleteChannel(UUID id) {
        validateExistenceChannel(id);
        fileChannelRepository.deleteById(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        boolean alreadyJoined = channel.getJoinedUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().add(user);
        user.getJoinedChannels().add(channel);

        fileUserRepository.save(user);
        fileChannelRepository.save(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        boolean alreadyLeaved = channel.getJoinedUsers().stream()
                .noneMatch(u -> u.getId().equals(userId));

        if (alreadyLeaved) {
            throw new IllegalArgumentException("채널에 속해 있지 않은 유저입니다.");
        }

        channel.getJoinedUsers().removeIf(u -> u.getId().equals(userId));
        user.getJoinedChannels().removeIf(c -> c.getId().equals(channelId));
        fileChannelRepository.save(channel);
        fileUserRepository.save(user);
    }

    @Override
    public List<Channel> readChannelsByUser(UUID userId) {
        return fileChannelRepository.findAll().stream()
                .filter(ch -> ch.getJoinedUsers().stream()
                        .anyMatch(u -> userId.equals(u.getId())))
                .toList();
    }


    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return fileChannelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }
}