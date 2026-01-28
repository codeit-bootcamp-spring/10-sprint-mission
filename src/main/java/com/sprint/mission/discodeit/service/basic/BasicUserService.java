package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public User createUser(String userName, String password, String email) {
        validateUserExist(userName);
        User user = new User(userName, password, email);
        userRepository.save(user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(userRepository.findAll());
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        return List.copyOf(userRepository.findAllByChannelId(channelId));
    }

    @Override
    public User updateUser(UUID userId, String userName, String password, String email) {
        validateUserExist(userName);
        User findUser = getUser(userId);
        Optional.ofNullable(userName)
                .ifPresent(findUser::updateUserName);
        Optional.ofNullable(password)
                .ifPresent(findUser::updatePassword);
        Optional.ofNullable(email)
                .ifPresent(findUser::updateEmail);
        userRepository.save(findUser);
        return findUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = getUser(userId);
        channelRepository.findAllByUserId(userId).forEach(channel -> {
            channel.removeUserId(userId);
            channelRepository.save(channel);
        });
        userRepository.deleteById(userId);
    }

    private void validateUserExist(String userName) {
        if(userRepository.findByName(userName).isPresent())
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
    }
}
