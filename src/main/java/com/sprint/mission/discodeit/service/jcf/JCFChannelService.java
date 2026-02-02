package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public JCFChannelService(ChannelRepository channelRepository,
                             MessageRepository messageRepository,
                             UserService userService) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID ownerId) {
        // 존재하는 유저인지 검증
        User owner = userService.findUserById(ownerId);
        // 채널 생성
        Channel channel = new Channel(name, owner);

        // 채널 추가
        return channelRepository.save(channel);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증, 존재하지 않으면 예외 발생
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));
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

        // 채널에 속한 메시지 수집
        List<Message> messages = new ArrayList<>(channel.getMessages());
        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);
        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageRepository.delete(m.getId()));
        // 채널에 가입된 모든 유저 탈퇴 처리
        List<User> users = new ArrayList<>(channel.getUsers());
        users.forEach(u -> u.leaveChannel(channel));

        // 관계를 정리한 후 채널 삭제, 저장소에서 제거
        channelRepository.delete(channel.getId());
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
