package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    @Override
    public User createUser(String username, String email, String password) {
        return userRepository.save(new User(username, email, password));
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        if (channelRepository.findById(channelId).isEmpty()) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        return userRepository.findAll().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User getUserInfoByUserId(UUID userId) {
        return findUserInfoById(userId);
    }

    @Override
    public User updateUserName(UUID userId, String newName) {
        User user = findUserInfoById(userId);
        user.updateUsername(newName);
        userRepository.save(user);

        // 메시지 파일 업데이트
        updateMessagesUsername(userId, newName);
        // 채널 파일 업데이트
        updateChannelsUsername(userId, newName);

        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        findUserInfoById(userId);
        userRepository.deleteById(userId);
    }

    private User findUserInfoById(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null 값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다."));
    }

    private void updateMessagesUsername(UUID userId, String newName) {
        messageRepository.findAll().stream()
                .filter(message -> message.getSentUser().getId().equals(userId))
                .forEach(message -> {
                    message.getSentUser().updateUsername(newName);
                    messageRepository.save(message);
                });
    }

    private void updateChannelsUsername(UUID userId, String newName) {
        channelRepository.findAll().forEach( channel -> {
            boolean updated = false;

            for (User joinedUser : channel.getJoinedUsers()) {
                if (joinedUser.getId().equals(userId)) {
                    joinedUser.updateUsername(newName);
                    updated = true;
                }
            }
            // 갱신 되면 저장
            if (updated) {
                channelRepository.save(channel);
            }
        });
    }
}
