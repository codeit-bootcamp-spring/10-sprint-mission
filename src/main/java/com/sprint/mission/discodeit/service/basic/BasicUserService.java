package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public User create(String username, String email, String password) {
        existsByEmail(email);
        User user = new User(username, email, password);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        return userRepository.findAll().stream().filter(user -> user.getChannelIds().stream()
                        .anyMatch(c -> c.equals(channelId)))
                .toList();
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String password, String username, String email) {
        User user = userRepository.findById(userId);
        validatePassword(user, password);

        if (email != null && !email.equals(user.getEmail())) {
            existsByEmail(email);
        }

        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);

        userRepository.save(user);

        return user;
    }

    @Override
    public User updatePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId);
        validatePassword(user, currentPassword);
        user.updatePassword(newPassword);
        userRepository.save(user);

        return user;
    }

    @Override
    public void delete(UUID userId, String password) {
        User user = userRepository.findById(userId);
        validatePassword(user, password);

        //유저 탈퇴시 유저 메세지 지우기
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
        for (Message message : messages) {
            messageRepository.delete(message);
        }

        //유저 탈퇴 시 채널에서 탈퇴
        for (UUID channelId : user.getChannelIds()) {
            Channel channel = channelRepository.findById(channelId);
            channel.deleteUser(userId);
            channelRepository.save(channel);
        }

        userRepository.delete(user);
    }

    //유저 이메일 중복체크
    private void existsByEmail(String email) {
        boolean exist = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
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
}
