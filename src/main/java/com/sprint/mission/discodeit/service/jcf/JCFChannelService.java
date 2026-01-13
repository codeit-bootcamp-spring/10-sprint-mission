package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap = new HashMap<>();
    private final UserService userService;
    // 생성자 주입
    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    // 중복 로직 분리
    private Channel findChannelByIdOrThrow(UUID channelId) {
        if (!channelMap.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 ID의 채널이 존재하지 않음. ID: " + channelId);
        }
        return channelMap.get(channelId);
    }

    private void validateDuplicateName(String channelName) {
        boolean isDuplicate = channelMap.values().stream()
                .anyMatch(ch -> ch.getChannelName().equals(channelName));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름 channelName: " + channelName);
        }
    }

    // Service Implementation
    // Create
    @Override
    public Channel createChannel(String name, UUID ownerId) {
        // 이름 유효성 검사
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름은 필수");
        }
        validateDuplicateName(name);

        // ID로 User 객체 조회 (참조 무결성 검사 겸 객체 확보)
        // UserService가 예외를 던져주므로 null 체크 불필요
        User owner = userService.findUserByUserId(ownerId);

        Channel channel = new Channel(name, owner);
        channelMap.put(channel.getId(), channel);

        System.out.println("채널 생성됨: " + channel.getChannelName() + " (ID: " + channel.getId() + ")");
        return channel;
    }

    // Read
    @Override
    public Channel findChannelById(UUID channelId) {
        return findChannelByIdOrThrow(channelId);
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel updateChannel(UUID channelId, String newChannelName) {
        Channel channel = findChannelByIdOrThrow(channelId);

        if (newChannelName == null || newChannelName.trim().isEmpty()) {
            throw new IllegalArgumentException("변경할 채널 이름이 비어있음");
        }

        // 중복 이름 체크 (자기 자신 제외)
        boolean isDuplicate = channelMap.values().stream()
                .anyMatch(ch -> !ch.getId().equals(channelId) && ch.getChannelName().equals(newChannelName));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름");
        }

        channel.updateChannelName(newChannelName);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelByIdOrThrow(channelId);
        channelMap.remove(channelId);
    }
}