package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.consistency.FileConsistencyManager;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileConsistencyManager fileConsistencyManager;
    private final UserRepository userRepository;

    public FileUserService(FileConsistencyManager fileConsistencyManager,
                           UserRepository userRepository) {
        this.fileConsistencyManager = fileConsistencyManager;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String nickname, String email) {
        // 유저 생성
        User user = new User(nickname, email);
        // 유저 추가
        return fileConsistencyManager.saveUser(user);
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        // 수정 대상 유저가 존재하는지 검색 및 검증
        User user = userRepository.findUserById(userId);
        // 유저 닉네임 수정
        user.updateUserNickname(newNickname);
        // 수정 내용 반영
        return fileConsistencyManager.saveUser(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 삭제 대상 유저가 존재하는지 검증
        User user = findUserById(userId);

        // 채널을 소유한 상태인지 확인, 채널을 소유한 상태에선 탈퇴 불가
        List<Channel> channels = user.getChannels();
        for (Channel channel : channels) {
            if (channel.getOwner().equals(user)) {
                throw new RuntimeException("채널을 소유한 상태에서는 탈퇴를 할 수 없습니다.");
            }
        }

        // 유저 삭제 및 삭제 내용 반영
        fileConsistencyManager.deleteUser(user);
    }
}
