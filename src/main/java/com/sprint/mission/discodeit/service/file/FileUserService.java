package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileUserService extends BaseFileService<User> implements UserService {

    private MessageService messageService;
    private ChannelService channelService;

    // Setter 주입, 순환 참조 문제 회피
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public FileUserService(Path directory) {
        super(directory);
    }

    @Override
    public void save(User user) {
        super.save(user);
    }

    @Override
    public User createUser(String username, String nickname, String email, String phoneNumber) {
        // 사용자 명이 null이거나 비었는지는 엔티티에서 검증함
        User newUser = new User(username, nickname, email, phoneNumber);
        save(newUser);

        return newUser;
    }

    @Override
    public User findById(UUID userId) {
        return super.findById(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream().
                filter(u -> u.getUsername().equals(username)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    public User updateUserProfile(UUID userId, String newUsername, String newNickname, String newEmail, String newPhoneNumber) {
        User user = findById(userId);

        if (isValid(user.getUsername(), newUsername)) user.updateUsername(newUsername);
        if (isValid(user.getNickname(), newUsername)) user.updateNickname(newNickname);
        if (isValid(user.getEmail(), newEmail)) user.updateEmail(newEmail);
        if (isValid(user.getPhoneNumber(), newPhoneNumber)) user.updatePhoneNumber(newPhoneNumber);
        save(user);
        syncUserChanges(user);

        return user;
    }

    @Override
    public User updateUserStatus(UUID userId, User.UserPresence newPresence, Boolean newMicrophoneIsOn, Boolean newHeadsetIsOn) {
        User user = findById(userId);

        if (isValid(user.getPresence(), newPresence)) user.changeStatus(newPresence);
        if (isValid(user.isMicrophoneOn(), newMicrophoneIsOn)) user.toggleMicrophone(newMicrophoneIsOn);
        if (isValid(user.isHeadsetOn(), newHeadsetIsOn)) user.toggleHeadset(newHeadsetIsOn);
        save(user);
        syncUserChanges(user); // 유저 변경 사항을 메시지와 채널에 반영

        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findById(userId);
        messageService.deleteMessagesByAuthorId(userId);
        user.getChannels().forEach(channel -> {
            user.leaveChannel(channel);
            channelService.save(channel);
                });
        Path filePath = getFilePath(userId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 삭제 실패: " + userId, e);
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.joinChannel(channel);
        channelService.save(channel);
        save(user);

    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.leaveChannel(channel);
        channelService.save(channel);
        save(user);
    }

    // Helper
    private boolean isValid(Object current, Object target) {
        if (target == null) return false;
        return !Objects.equals(current, target);
    }

    private void syncUserChanges(User user) {
        // 1. 유저가 작성한 모든 메시지 업데이트
        messageService.findMessagesByAuthor(user.getId()).forEach(msg -> {
            msg.updateAuthor(user);
            messageService.save(msg);
        });

        // 2. 유저가 가입한 모든 채널의 유저 목록 업데이트
        user.getChannels().forEach(channel -> {
            Channel lastestChannel = channelService.findById(channel.getId());
            lastestChannel.updateUserInSet(user);
            channelService.save(lastestChannel);
        });
    }
}
