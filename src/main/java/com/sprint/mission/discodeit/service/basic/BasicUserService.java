//package com.sprint.mission.discodeit.service.basic;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.listener.UserLifecycleListener;
//
//import java.util.*;
//
//public class BasicUserService implements UserService {
//    private final UserRepository userRepository;
//    private final List<UserLifecycleListener> listeners = new ArrayList<>();
//
//    // 생성자 주입 (Repository 인터페이스에만 의존)
//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void addListener(UserLifecycleListener listener) {
//        listeners.add(listener);
//    }
//
//    @Override
//    public User createUser(String username) {
//        // 비즈니스 로직 1: 중복 검사
//        if (userRepository.existsByUsername(username)) {
//            throw new IllegalArgumentException("이미 존재하는 유저 이름입니다. (username: " + username + ")");
//        }
//
//        // 비즈니스 로직 2: 객체 생성
//        User user = new User(username);
//
//        // 저장 로직 위임
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User findUserByUserId(UUID userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 id의 유저가 존재하지 않습니다. (userId: " + userId + ")"));
//    }
//
//    @Override
//    public List<User> findAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User updateUser(UUID userId, String newUsername) {
//        User user = findUserByUserId(userId);
//
//        if (user.getUsername().equals(newUsername)) {
//            return user;
//        }
//
//        if (userRepository.existsByUsername(newUsername)) {
//            throw new IllegalArgumentException("이미 존재하는 유저 이름입니다. (username: " + newUsername + ")");
//        }
//
//        user.updateUsername(newUsername);
//        return userRepository.save(user);
//    }
//
//    @Override
//    public void deleteUser(UUID userId) {
//        // 존재 확인
//        findUserByUserId(userId);
//
//        // 비즈니스 로직: 리스너 알림 (연관 데이터 삭제 처리)
//        for (UserLifecycleListener listener : listeners) {
//            listener.onUserDelete(userId);
//        }
//
//        // 저장소 삭제 위임
//        userRepository.delete(userId);
//        System.out.println("유저 삭제 완료 (Basic).");
//    }
//}