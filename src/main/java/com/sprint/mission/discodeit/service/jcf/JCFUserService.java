package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.DataStore;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final DataStore dataStore;
    private final Map<UUID, User> data;

    public JCFUserService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.data = dataStore.getUserData();
    }

    @Override
    public User createUser(String nickname, String email) {
        // 새로운 유저 생성
        User user = new User(nickname, email);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        // 존재하는 유저인지 검색 및 검증, 존재하지 않으면 예외 발생
        User user = data.get(userId);
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        // 전체 유저 목록 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        // 수정 대상 유저가 실제로 존재하는지 검색 및 검증
        User user = findUserById(userId);
        // 유저 닉네임 수정
        return user.updateUserNickname(newNickname);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 삭제 대상 유저가 실제로 존재하는지 검증
        User user = findUserById(userId);

        // 채널을 소유한 상태인지 확인, 채널을 소유한 상태에선 탈퇴 불가
        List<Channel> channels = user.getChannels();
        for (Channel channel : channels) {
            if (channel.getOwner().equals(user)) {
                throw new RuntimeException("채널을 소유한 상태에서는 탈퇴를 할 수 없습니다.");
            }
        }

        // 유저 삭제를 위해 해당 유저의 관계 정리
        dataStore.cleanupUserRelations(user);

        // 관계를 정리한 후 유저 삭제, 저장소에서 제거
        data.remove(userId);
    }
}
