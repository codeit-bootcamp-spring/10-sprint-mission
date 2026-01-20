//package com.sprint.mission.service.file;
//
//import com.sprint.mission.entity.Channel;
//import com.sprint.mission.entity.User;
//import com.sprint.mission.service.UserService;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class FileUserService extends BaseFileService implements UserService {
//    public FileUserService(Path directory) {
//        super(directory);
//    }
//
//    @Override
//    public User create(String name, String email) {
//        Map<UUID, User> data = loadData();
//        validateNotDuplicatedEmail(data, email);
//
//        User user = new User(name, email);
//        Path filePath = getFilePath(user);
//
//        save(filePath, user);
//        return user;
//    }
//
//    @Override
//    public User findById(UUID id) {
//        Map<UUID, User> data = loadData();
//        return getUserOrThrow(data, id);
//    }
//
//    @Override
//    public List<User> findAll() {
//        return List.copyOf(loadData().values());
//    }
//
//    @Override
//    public List<Channel> findByUserId(UUID userId) {
//        User user = findById(userId);
//        return user.getChannels();
//    }
//
//    @Override
//    public User update(UUID id, String name) {
//        User user = findById(id);
//        user.updateName(name);
//
//        save(getFilePath(user), user);
//        return user;
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        User user = findById(id);
//        Path filePath = getFilePath(user);
//        delete(filePath);
//    }
//
//    private void validateNotDuplicatedEmail(Map<UUID, User> data, String email) {
//        boolean isDuplicatedEmail = data.values().stream()
//                .anyMatch(user -> user.getEmail().equals(email));
//
//        if (isDuplicatedEmail) {
//            throw new IllegalArgumentException("이미 해당 이메일로 생성된 계정이 있습니다. 다시 시도해주세요.");
//        }
//    }
//
//    private User getUserOrThrow(Map<UUID, User> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
//        }
//        return data.get(id);
//    }
//
//    private Map<UUID, User> loadData() {
//        return load(User::getId);
//    }
//
//    private Path getFilePath(User user) {
//        return directory.resolve(user.getId().toString().concat(".ser"));
//    }
//}
