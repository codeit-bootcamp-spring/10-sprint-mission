package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicUserService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String email, String password) {
        existsByEmail(email);
        User user = new User(username, email, password);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findAllUser().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        return userRepository.findAllUser().stream().filter(user -> user.getChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAllUser();
    }

    @Override
    public User update(UUID userId, String password, String username, String email) {
        existsByEmail(email);

        User user = userRepository.findUserById(userId);
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
        User user = userRepository.findUserById(userId);
        validatePassword(user, currentPassword);
        user.updatePassword(newPassword);
        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UUID userId, String password) {
        User user = userRepository.findUserById(userId);
        validatePassword(user, password);

        List<Channel> channels = new ArrayList<>(user.getChannels());

        leaveUserFromChannels(user, channels);

        channels.forEach(channelRepository::save);

        userRepository.delete(user);
    }

    //유저 이메일 중복체크
    private void existsByEmail(String email) {
        boolean exist = userRepository.findAllUser().stream()
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

    private void leaveUserFromChannels(User user, List<Channel> channels) {
        for (Channel channel : channels) {
            channel.leave(user);
            user.leave(channel);
        }
    }
}
