package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.ChannelRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

// Service Implementation
public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap = new HashMap<UUID, Channel>();

    // id로 Channel 객체 조회 메서드 - 해당 id의 Channel 있으면 Channel 객체 반환. 없으면 예외 발생
    private Channel findChannelByIdOrThrow(UUID channelId) {
        if (!channelMap.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 ID의 채널이 존재하지 않습니다. id: " + channelId);
        }
        return channelMap.get(channelId);
    }
    // 채널 이름 중복 여부 판단 메서드 - 채널 이름 중복되면 예외 발생
    private void validateDuplicateName(String channelName) {
        // 이름이 같은 채널을 찾아서 Optional<Channel> 형태로 반환
        Optional<Channel> duplicateChannel = channelMap.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst();
        if (duplicateChannel.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. channelName: " + channelName
                    + " id: " + duplicateChannel.get().getId());
        }
    }

    // Create - 채널 생성 / 채널장(서버장)인 Owner 필수 (유저 없는 채널 존재 불가능)
    @Override
    public Channel createChannel(String name, User owner) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름은 필수");
        }
        validateDuplicateName(name);

        Channel channel = new Channel(name, owner);
        channelMap.put(channel.getId(), channel);

        return channel;
    }

    // Read - 채널 단건 조회 / 특정 채널 조회 (채널 가입 등을 위한 검색)
    @Override
    public Channel findChannelById(UUID channelId) {
        return findChannelByIdOrThrow(channelId);
    }
    // Read - 모든 채널 조회 / 전체 채널 조회 (채널 가입 등을 위한 탐색창)
    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channelMap.values());
    }

    // Update - 채널 이름 수정
    @Override
    public Channel updateChannel(UUID channelId, String newChannelName) {
        Channel channel = findChannelByIdOrThrow(channelId);

        if (newChannelName == null || newChannelName.trim().isEmpty()) {
            throw new IllegalArgumentException("변경할 채널 이름이 비어있습니다.");
        }

        Optional<Channel> duplicateChannel = channelMap.values().stream()
                .filter(ch -> !ch.getId().equals(channelId) && ch.getChannelName().equals(newChannelName))
                .findFirst();
        if (duplicateChannel.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. channelName: " + newChannelName
                    + " id: " + duplicateChannel.get().getId());
        }

        channel.updateChannelName(newChannelName);
        return channel;
    }

    // Delete - 채널 삭제 / 채널 내 메시지 + 채널-유저 관계 해제
    @Override
    public void deleteChannel(UUID channelId) {
        findChannelByIdOrThrow(channelId);
        channelMap.remove(channelId);
    }
    // Delete - 특정 채널장의 모든 채널 삭제
    @Override
    public void deleteChannelsByOwnerId(UUID ownerId) {
        // 채널 맵의 값들 중 방장(Owner)의 ID가 ownerId와 같은 것을 모두 삭제
        channelMap.values().removeIf(channel -> channel.getOwner().getId().equals(ownerId));

        System.out.println("해당 유저가 채널장(Owner)인 모든 채널을 삭제했습니다. ownerId: " + ownerId);
    }
}