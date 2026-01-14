package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final UserService userService;
    private final Map<UUID, Channel> data;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, User owner) {
        // 존재하는 유저인지 검증
        userService.findUserById(owner.getId());
        // 채널 생성
        Channel channel = new Channel(name, owner);

        // 채널 추가 및 채널 참가
        data.put(channel.getId(), channel);
        userService.joinChannel(channel, owner.getId());
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        // 존재하는 채널인지 검색 및 검증, 존재하지 않으면 예외 발생
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        // 전체 채널 목록 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannelName(UUID channelId, String newName) {
        // 수정 대상 채널이 실제로 존재하는지 검색 및 검증
        Channel channel = findChannelById(channelId);
        // 채널 이름 수정
        return channel.updateChannelName(newName);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        // 삭제 대상 채널이 실제로 존재하는지 검색 및 검증
        findChannelById(channelId);
        // 채널 삭제 전, 해당 채널에 가입된 모든 유저의 채널 목록에서 먼저 제거
        userService.removeChannelFromJoinedUsers(channelId);
        // 모든 유저와의 관계를 정리한 후 채널 삭제, 저장소에서 제거
        data.remove(channelId);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        // 실제로 존재하는 채널인지 검증
        Channel channel = findChannelById(channelId);
        // 채널 가입
        userService.joinChannel(channel, userId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        // 실제로 존재하는 채널인지 검증
        Channel channel = findChannelById(channelId);
        // 채널에서 해당 유저 탈퇴 처리
        userService.leaveChannel(channel, userId);
    }
}
