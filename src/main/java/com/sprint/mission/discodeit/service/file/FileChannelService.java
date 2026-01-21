package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.consistency.FileConsistencyManager;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final FileConsistencyManager fileConsistencyManager;
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public FileChannelService(FileConsistencyManager fileConsistencyManager,
                              ChannelRepository channelRepository,
                              UserService userService) {
        this.fileConsistencyManager = fileConsistencyManager;
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID ownerId) {
        // 존재하는 유저인지 검증
        User owner = userService.findUserById(ownerId);
        // 채널 생성
        Channel channel = new Channel(name, owner);

        // 채널 저장
        return fileConsistencyManager.saveChannel(channel);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelRepository.findChannelById(channelId);
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
        return fileConsistencyManager.saveChannel(channel);
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        // 삭제 대상 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널 권한 확인, 채널 소유자만 삭제 가능
        User user = userService.findUserById(userId);
        if (!channel.getOwner().equals(user)) {
            throw new RuntimeException("해당 채널에 대한 권한이 없습니다.");
        }

        // 채널 삭제 및 삭제 내용 반영
        fileConsistencyManager.deleteChannel(channel);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        // 유저가 가입하려는 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 가입 여부 확인, 가입되어 있는 유저라면 예외 발생
        if (channel.getUsers().contains(user)) {
            throw new RuntimeException("이미 채널에 가입한 유저입니다.");
        }
        // 가입 여부 확인, 가입되어 있는 채널이라면 예외 발생
        if (user.getChannels().contains(channel)) {
            throw new RuntimeException("이미 채널에 가입한 유저입니다.");
        }

        // 채널 가입
        channel.addUser(user);
        // 채널 가입 반영
        fileConsistencyManager.channelManagement(channel, user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        // 유저가 탈퇴하려는 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 가입 여부 확인, 가입되어 있지 않은 유저라면 예외 발생
        if (!channel.getUsers().contains(user)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }
        // 가입 여부 확인, 가입되어 있지 않은 채널이라면 예외 발생
        if (!user.getChannels().contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 채널 탈퇴
        channel.removeUser(user);
        // 채널 탈퇴 반영
        fileConsistencyManager.channelManagement(channel, user);
    }

    @Override
    public List<User> getMembers(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널에 속한 유저 목록 반환
        return channel.getUsers();
    }
}
