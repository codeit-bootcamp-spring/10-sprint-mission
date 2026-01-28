package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    private static final String ROOT_PATH = System.getProperty("user.dir") + "/data";
    private final Path dirPath;

    public FileUserService() {
        this(ROOT_PATH);
    }

    public FileUserService(String rootPath) {
        this.dirPath = Paths.get(rootPath, "user");
        init();
    }

    @Override
    public User create(String username, String email, String password) {
        existsByEmail(email);
        User user = new User(username, email, password);
        save(user);
        return user;
    }

    public User findUserById(UUID userId) {
        return load().stream()
                .filter(user -> user.getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public User findUserByEmail(String email) {
        return load().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    //특정 채널에 참가한 사용자 리스트 조회
    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        return load().stream().filter(user -> user.getChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public List<User> findAllUser() {
        return load();
    }

    @Override
    public User update(UUID userId, String password, String username, String email) {
        existsByEmail(email);

        User user = findUserById(userId);
        validatePassword(user, password);

        if (email != null && !email.equals(user.getEmail())) {
            existsByEmail(email);
        }

        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);

        save(user);
        return user;
    }

    @Override
    public User updatePassword(UUID userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);
        validatePassword(user, currentPassword);
        user.updatePassword(newPassword);
        save(user);
        return user;
    }

    @Override
    public void delete(UUID userId, String password) {

        User user = findUserById(userId);
        validatePassword(user, password);

        new ArrayList<>(user.getChannels()).forEach(channel -> {
            channel.leave(user);
            user.leave(channel);
            //ChannelService의 .ser파일들을 수정할 수 없음
        });

        File file = new File(dirPath.toFile(), user.getId().toString() + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }

    //유저 이메일 중복체크
    private void existsByEmail(String email) {
        boolean exist = load().stream().anyMatch(user -> user.getEmail().equals(email));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다: " + email);
        }
    }

    //비밀번호 검증
    private void validatePassword(User user, String inputPassword) {
        if (!user.getPassword().equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("유저 데이터 폴더 생성 실패", e);
            }
        }
    }

    private void save(User user) {
        File file = new File(dirPath.toFile(), user.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 실패", e);
        }
    }

    private List<User> load() {
        if(!Files.exists(dirPath)) {
            return new ArrayList<>();
        }
        try {
            List<User> list = Files.list(dirPath)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (User) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
