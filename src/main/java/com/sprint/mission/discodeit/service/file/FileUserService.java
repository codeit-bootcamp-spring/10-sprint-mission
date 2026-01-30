package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final FileUserRepository fileUserRepository;

    public FileUserService(FileUserRepository fileUserRepository) {
        this.fileUserRepository = fileUserRepository;
    }

    @Override
    public User createUser(String userName, String userEmail) {
        Validators.validationUser(userName, userEmail);
        validateDuplicationEmail(userEmail);
        User user = new User(userName, userEmail);
        return fileUserRepository.save(user);
    }

    @Override
    public User find(UUID id) {
        return validateExistenceUser(id); // 비즈니스 로직
    }

    @Override
    public List<User> findAll() {
        return fileUserRepository.findAll();
    } // 저장 로직

    @Override
    public User update(UUID id, String userName, String userEmail) {
        User user = validateExistenceUser(id);// 비즈니스 로직

        Optional.ofNullable(userName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
                    user.updateUserName(name);
                }); //비즈니스 로직 + 저장 로직
        Optional.ofNullable(userEmail)
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
                    validateDuplicationEmail(email);
                    user.updateUserEmail(email);
                }); // 비즈니스 로직 + 저장 로직

        return fileUserRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        validateExistenceUser(userId);
        fileUserRepository.deleteById(userId);
    }



    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        return fileUserRepository.findAll().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(ch -> channelId.equals(ch.getId())))
                .toList();
    } // 비즈니스 로직 + 저장 로직

    private void validateDuplicationEmail(String userEmail) {
        if(fileUserRepository.findAll().stream()
                .anyMatch(user -> userEmail.equals(user.getUserEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    } // 비즈니스 로직 + 저장 로직


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다."); // 비즈니스 로직
        return fileUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    } // 비즈니스 로직 + 저장 로직
}
