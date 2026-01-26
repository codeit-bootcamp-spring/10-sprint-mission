package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 채널을 찾을 수 없습니다"));
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data);
    }
    /**
     * 특정 유저가 참여 중인 채널 목록을 조회합니다.
     *
     * <p>
     * ⚠️ 주의:
     * 이 메서드는 {@code UserService}와의 순환 참조를 피하기 위해
     * 내부에서 유저 존재 여부를 검증하지 않습니다.
     * 따라서 {@code userId}가 실제로 존재하지 않더라도
     * 예외를 던지지 않고 빈 리스트를 반환할 수 있습니다.
     * </p>
     *
     * <p>
     * 📌 권장 사항:
     * 유저의 채널 목록을 조회하려는 경우,
     * 이 메서드를 직접 호출하기보다는
     * 상위 레이어(예: {@code DiscordService}, {@code ChatCoordinator})에서
     * 먼저 유저 존재 여부를 검증한 뒤 호출하거나,
     * 다음과 같은 대안을 사용하는 것을 권장합니다.
     * </p>
     *
     * <ul>
     *   <li>{@code UserService.find(userId)} 후 {@code user.getJoinedChannels()} 사용</li>
     *   <li>유저를 기준으로 채널을 관리하는 전용 조회 메서드</li>
     * </ul>
     *
     * <p>
     * 본 메서드는 채널 중심 조회가 필요한 내부 로직에서만
     * 제한적으로 사용하는 것을 의도합니다.
     * </p>
     *
     * @param userId 조회할 유저의 ID
     * @return 해당 유저가 참여 중인 채널 목록 (없을 경우 빈 리스트)
     */
    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        return data.stream()
                .filter(channel -> channel.getUserList().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = read(id);
        return channel.update(name);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = read(id);
        data.remove(channel);
    }

    @Override
    public void deleteUserInChannels(UUID userId) {
        getChannelsByUser(userId)
                .forEach(channel ->
                        channel.getUserList().removeIf(user -> user.getId().equals(userId)));
        }
    }

