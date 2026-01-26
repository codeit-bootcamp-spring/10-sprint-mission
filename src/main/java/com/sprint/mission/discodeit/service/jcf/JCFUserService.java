package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(String name, String email, String password) {
        User user = new User(name, email, password);
        data.add(user);
        return user;
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User read(UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 유저를 찾을 수 없습니다"));
    }
    /**
     * 특정 채널에 참여 중인 유저 목록을 조회합니다.
     *
     * <p>
     * ⚠️ 주의:
     * 이 메서드는 {@code ChannelService}와의 순환 참조를 피하기 위해
     * 내부에서 채널 존재 여부를 검증하지 않습니다.
     * 따라서 {@code channelId}가 실제로 존재하지 않더라도
     * 예외를 던지지 않고 빈 리스트를 반환할 수 있습니다.
     * </p>
     *
     * <p>
     * 📌 권장 사항:
     * 채널의 유저 목록을 조회하려는 경우,
     * 이 메서드를 직접 호출하기보다는
     * 상위 레이어(예: {@code DiscordService})에서
     * 먼저 채널 존재 여부를 검증한 뒤 호출하거나,
     * 다음과 같은 대안을 사용하는 것을 권장합니다.
     * </p>
     *
     * <ul>
     *   <li>{@code UserService.read(userId)}를 통해 유저 객체를 가져온 후 필터링 진행</li>
     *   <li>유저 엔티티 내에 채널 목록 필드가 있다면 해당 필드 사용</li>
     * </ul>
     *
     * <p>
     * 본 메서드는 유저 중심 조회가 필요한 내부 로직에서만
     * 제한적으로 사용하는 것을 의도합니다.
     * </p>
     *
     * @param channelId 조회할 채널의 ID
     * @return 해당 채널에 참여 중인 유저 목록 (없을 경우 빈 리스트)
     */
    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        return data.stream()
                .filter(user -> user.getChannelList().stream() // 유저는 여러 채널을 가질 수 있음
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User update(UUID id, String name, String email, String password) {
        User user = read(id);
        return user.update(name, email, password);
    }

    @Override
    public void delete(UUID id) {
        User user = read(id);
        data.remove(user);
    }

    @Override
    public void deleteUsersInChannel(UUID channelId) {
        List<User> userList = getUsersByChannel(channelId);
        for (User user : userList) {
            user.getChannelList().removeIf(channel -> channel.getId().equals(channelId));
        }

    }
}
