package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;
import com.sprint.mission.discodeit.DTO.UserRegitrationRecord;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository; // 아직 인터페이스 구현체가 없어서 bean을 못찾음.
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserRegitrationRecord req) {

        if(userRepository.findAll().stream().anyMatch(u -> req.userName().equals(u.getUsername()) || req.email().equals(u.getEmail()))){
            throw new IllegalStateException("이미 존재하는 이름 or 이메일입니다.");
        }

        UUID profileID = null;
        if(req.binaryContentRecord() != null){
            BinaryContentRecord p = req.binaryContentRecord();
            BinaryContent binaryContent = new BinaryContent(p.contentType(), p.bytes());
            profileID = binaryContentRepository.save(binaryContent).getId();
        }

        User user = new User(req.userName(), req.email(), req.password(), profileID);
        User savedUser = userRepository.save(user);

        userStatusRepository.save(new UserStatus(savedUser.getId()));

        return savedUser;
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
