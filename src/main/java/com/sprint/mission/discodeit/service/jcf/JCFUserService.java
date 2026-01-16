package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.listener.UserLifecycleListener;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Service Implementation
public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new HashMap<UUID, User>();
    private final List<UserLifecycleListener> listeners = new ArrayList<UserLifecycleListener>(); // 리스너 목록 추가

    public void addListener(UserLifecycleListener listener) { // 리스너 등록 메서드
        this.listeners.add(listener);
    }
    // id로 User 객체 조회 메서드 - 해당 id의 User 있으면 User 객체 반환. 없으면 예외 발생
    private User findUserByIdOrThrow(UUID id) {
        if (!userMap.containsKey(id)) {
            throw new IllegalArgumentException("해당 id의 User가 존재하지 않습니다. (id: " + id + ")");
        }
        return userMap.get(id);
    }

    // Create - 사용자 등록 / 유저를 생성 / 유저가 회원가입을 하여 계정(User)을 생성한다.
    @Override
    public User createUser(String newUsername) {
        // 중복 이름 검사
        boolean isDuplicate = userMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(newUsername));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 유저(User) 이름입니다. username: " + newUsername);
        }

        User user = new User(newUsername);
        userMap.put(user.getId(), user);

        return user;
    }

    // Read - 사용자 단건 조회 / 유저가 특정 유저를 조회한다. (친구 추가 등을 위해)
    @Override
    public User findUserByUserId(UUID id) {
        return findUserByIdOrThrow(id);
    }
    // Read - 사용자 전체 정보 조회 (시스템관리자용)
    @Override
    public List<User> findAllUsers() {
        return new ArrayList<User>(userMap.values());
    }

    // Update - 사용자 정보 수정 / 유저 이름 변경 / 유저가 이름을 변경한다.
    @Override
    public User updateUser(UUID id, String newUsername) {
        // 수정할 User의 id가 존재하지 않는 id일 경우 예외 발생
        User user = findUserByIdOrThrow(id);

        // 중복 이름 검사 (자기 자신은 제외하고 체크)
        boolean isDuplicate = userMap.values().stream()
                .anyMatch(u -> !u.getId().equals(id) && u.getUsername().equals(newUsername) );
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다. username: " + newUsername + " id: " + id );
        }

        user.updateUsername(newUsername);
        return user;
    }

    // Delete - 사용자 삭제 / 유저 탈퇴 / 유저가 탈퇴한다. / 시스템 관리자가 해당 유저를 삭제시킨다.
    @Override
    public void deleteUser(UUID id) {
        User user = findUserByIdOrThrow(id);

        // 삭제 이벤트 전파 (다른 서비스들에게 알림)
        for (UserLifecycleListener listener : listeners) {
            listener.onUserDelete(id);
        }

        // 유저 본체 삭제
        userMap.remove(id);
        System.out.println("유저(User) 삭제 완료하였습니다. (유저 작성 메시지삭제, 유저 참여 중 채널 관계 삭제)");
        System.out.println("\tusername: " + user.getUsername() + " (id: " + id + ")");
    }
}