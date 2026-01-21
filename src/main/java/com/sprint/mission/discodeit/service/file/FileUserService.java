package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.listener.UserLifecycleListener;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private Map<UUID, User> userDB = new HashMap<>();
    private final File file = new File("users.ser"); // 저장할 파일명

    private final List<UserLifecycleListener> listeners = new ArrayList<>();

    public FileUserService() {
        if (file.exists()) {
            load();
        } else {
            persist();
        }
    }

    // 파일에서 Map 객체를 읽어오는 메소드 (역직렬화)
    @SuppressWarnings("unchecked") // 컴파일러 경고 무시
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
             this.userDB = (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("데이터 로드 중 오류 발생: " + e.getMessage());
            this.userDB = new HashMap<>(); // 오류 시 빈 Map 으로 초기화 // TODO: 다른 방법 있는지 고려 필요
        }
    }

    // Map 객체를 파일에 저장하는 메소드 (직렬화)
    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.userDB);
        } catch (IOException e) {
            System.err.println("데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // JCFUserService와 동일하게 리스너 지원 (Main 실행 호환성 위함)
    public void addListener(UserLifecycleListener listener) {
        listeners.add(listener);
    }

    private void validateDuplicateUsername(String username) {
        boolean isDuplicate = userDB.values().stream()
                .anyMatch(user ->user.getUsername().equals(username));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 유저 이름입니다. (username: " + username + " )");
        }
    }

    @Override
    public User createUser(String username) {
        validateDuplicateUsername(username);
        User user = new User(username);
        userDB.put(user.getId(), user);
        persist(); // 변경 사항 저장
        return user;
    }

    @Override
    public User findUserByUserId(UUID userId) {
        if (!userDB.containsKey(userId)) {
            throw new IllegalArgumentException("해당 id의 유저가 존재하지 않습니다. (userId: " + userId + " )");
        }
        return userDB.get(userId);
    }
    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(userDB.values());
    }

    @Override
    public User updateUser(UUID userId, String newUsername) {
        User user = findUserByUserId(userId);

        if (user.getUsername().equals(newUsername)) {
            return user;
        }

        validateDuplicateUsername(newUsername);
        user.updateUsername(newUsername);
        persist(); // 변경 사항 저장
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserByUserId(userId);

        for (UserLifecycleListener listener : listeners) {
            listener.onUserDelete(userId);
        }

        userDB.remove(userId);
        persist(); // 변경 사항 저장
        System.out.println("유저 삭제 완료되었습니다. (File)");
    }
}
