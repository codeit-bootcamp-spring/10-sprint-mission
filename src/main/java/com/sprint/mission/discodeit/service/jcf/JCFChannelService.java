package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.consistency.JCFConsistencyManager;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final JCFConsistencyManager jcfConsistencyManager;
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(JCFConsistencyManager jcfConsistencyManager,
                             ChannelRepository channelRepository,
                             UserService userService) {
        this.jcfConsistencyManager = jcfConsistencyManager;
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID ownerId) {
        // 존재하는 유저인지 검증
        User owner = userService.findUserById(ownerId);
        // 채널 생성
        Channel channel = new Channel(name, owner);

        // 채널 추가
        return channelRepository.saveChannel(channel);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증, 존재하지 않으면 예외 발생
        Channel channel = channelRepository.findChannelById(channelId);
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        // 전체 채널 목록 반환
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
        return channel.updateChannelName(newName);
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        // 삭제 대상 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널 권한 확인, 채널 소유자만 삭제 가능
        if (!channel.getOwner().getId().equals(userId)) {
            throw new RuntimeException("해당 채널에 대한 권한이 없습니다.");
        }

        // 채널 삭제를 위해 해당 채널의 관계 정리
        jcfConsistencyManager.cleanupChannelRelations(channel);

        // 관계를 정리한 후 채널 삭제, 저장소에서 제거
        channelRepository.deleteChannel(channel.getId());
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        // 유저가 가입하려는 채널이 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 가입 여부 확인, 이미 존재하는 유저라면 예외 발생
        if (channel.getUsers().contains(user)) {
            throw new RuntimeException("이미 채널에 가입한 유저입니다.");
        }
        // 가입 여부 확인, 이미 가입한 채널이라면 예외 발생
        if (user.getChannels().contains(channel)) {
            throw new RuntimeException("이미 가입한 채널입니다.");
        }

        // 채널 가입
        channel.addUser(user);
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
    }

    @Override
    public List<User> getMembers(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널에 속한 유저 목록 반환
        return channel.getUsers();
    }
}
