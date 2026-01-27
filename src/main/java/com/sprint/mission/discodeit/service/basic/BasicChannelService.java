package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(UserRepository userRepository,
                               ChannelRepository channelRepository,
                               MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel createChannel(String name, UUID ownerId) {
        // 존재하는 유저인지 검색 및 검증
        User owner = userRepository.findUserById(ownerId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 채널 생성
        Channel channel = new Channel(name, owner);

        // 채널 저장 반영
        channelRepository.saveChannel(channel);
        userRepository.saveUser(owner);

        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelRepository.findChannelById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String newName) {
        // 수정 대상 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널 권한 확인, 채널 소유자만 수정 가능
        if (!channel.getOwner().getId().equals(userId)) {
            throw new RuntimeException("해당 채널에 대한 권한이 없습니다.");
        }

        // 채널 이름 수정
        channel.updateChannelName(newName);

        // 수정 내용 반영
        channelRepository.saveChannel(channel);
        channel.getUsers().forEach(userRepository::saveUser);
        channel.getMessages().forEach(messageRepository::saveMessage);

        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        // 삭제 대상 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널 권한 확인, 채널 소유자만 삭제 가능
        userRepository.findUserById(userId);
        if (!channel.getOwner().getId().equals(userId)) {
            throw new RuntimeException("해당 채널에 대한 권한이 없습니다.");
        }

        // 채널 삭제 및 삭제 내용 반영
        // 채널의 유저 목록과 메시지 목록 조회
        List<User> users = new ArrayList<>(channel.getUsers());
        List<Message> messages = new ArrayList<>(channel.getMessages());

        // 채널에 가입된 유저들 탈퇴 처리
        for (User user : users) {
            user.leaveChannel(channel);
            userRepository.saveUser(user);
        }
        // 채널에 작성된 메시지 삭제 처리
        for (Message message : messages) {
            message.removeFromChannelAndUser();
            messageRepository.deleteMessage(message.getId());
        }

        // 채널 삭제
        channelRepository.deleteChannel(channel.getId());
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        // 유저가 가입하려는 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 가입 여부 확인, 가입되어 있는 유저라면 예외 발생
        if (channel.getUsers().contains(user) || user.getChannels().contains(channel)) {
            throw new RuntimeException("이미 채널에 가입한 유저입니다.");
        }

        // 채널 가입
        channel.addUser(user);

        // 채널 가입 반영
        channelRepository.saveChannel(channel);
        userRepository.saveUser(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        // 유저가 탈퇴하려는 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 가입 여부 확인, 가입되어 있지 않은 유저라면 예외 발생
        if (!channel.getUsers().contains(user) || !user.getChannels().contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 채널 탈퇴
        channel.removeUser(user);

        // 채널 탈퇴 반영
        channelRepository.saveChannel(channel);
        userRepository.saveUser(user);
    }

    @Override
    public List<User> getMembers(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널에 속한 유저 목록 반환
        return channel.getUsers();
    }
}
