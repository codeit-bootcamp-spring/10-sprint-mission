package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final UserRepository userRepository = new FileUserRepository();
    private final ChannelRepository channelRepository = new FileChannelRepository();

    @Override
    public User createUser(String username, String password, String email) {
        User newUser = new User(username, password, email);

        userRepository.save(newUser);

        System.out.println(username + "님 회원가입 완료되었습니다.");
        return newUser;
    }

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new IllegalArgumentException("해당 유저를 다찾을 수 없습니다"));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserInfo(UUID id, String newUsername, String newEmail) {
        User targetUser = findUserById(id);

        if (newUsername == null && newEmail == null) {
            throw new IllegalArgumentException("둘 중 하나는 입력해야 합니다.");
        }

        Optional.ofNullable(newUsername)
                .filter(name -> name.contains(" "))
                .ifPresent(name -> {
                    throw new IllegalArgumentException("띄어쓰기는 포함할 수 없습니다.");
                });

        Optional.ofNullable(newEmail)
                .filter(email -> !isValidEmail(email))
                .ifPresent(email -> {
                    throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
                });

        // 실제 수정 로직
        Optional.ofNullable(newUsername).ifPresent(name -> {
            targetUser.updateUsername(name);
            System.out.println("이름이 변경되었습니다: " + targetUser.getUsername());
        });

        Optional.ofNullable(newEmail).ifPresent(email -> {
            targetUser.updateEmail(email);
            System.out.println("이메일이 변경되었습니다: " + targetUser.getEmail());
        });

        userRepository.save(targetUser);

        return targetUser;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public User changePassword(UUID id, String newPassword) {
        User targetUser = findUserById(id);
        targetUser.updatePassword(newPassword);

        userRepository.save(targetUser);

        return targetUser;
    }

    @Override
    public void deleteUser(UUID id) {
        User targetUser = findUserById(id);

        for (Channel channel : targetUser.getMyChannels()) {
            channel.getParticipants().remove(targetUser);
            channelRepository.save(channel);
        }
        userRepository.delete(id);

        System.out.println(targetUser.getUsername() + "님 삭제 완료되었습니다");
    }

    @Override
    public List<User> findParticipants(UUID channelID) {
        return userRepository.findByChannelId(channelID);
    }
}