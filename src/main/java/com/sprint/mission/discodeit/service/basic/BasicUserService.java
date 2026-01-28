package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;
import com.sprint.mission.discodeit.DTO.UserRegitrationRecord;
import com.sprint.mission.discodeit.DTO.UserReturnDTO;
import com.sprint.mission.discodeit.DTO.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public UserReturnDTO find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus userStatus = userStatusRepository.findByID(userId);

        return new UserReturnDTO(user.getId(),
                user.getProfileID(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

    }

    @Override
    public List<UserReturnDTO> findAll() {
        List<UserReturnDTO> userDTOList;
        return userRepository
                .findAll()
                .stream()
                .map(u -> {
                    UserStatus userStatus = userStatusRepository.findByID(u.getId());
                    boolean online = userStatus.isOnline();
                    Instant lastActiveAt = userStatus.getLastActiveAt();
                    return new UserReturnDTO(u.getId(),
                            u.getProfileID(),
                            u.getUsername(),
                            u.getEmail(),
                            online,
                            lastActiveAt,
                            u.getCreatedAt(),
                            u.getUpdatedAt());
                }).toList();
    }

    @Override
    public User update(UserUpdateDTO req) {
        User user = userRepository.findById(req.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + req.id() + " not found"));

        if(userRepository
                .findAll()
                .stream()
                .anyMatch(u -> !u.getId().equals(req.id()) && (req.name().equals(u.getUsername()) || req.email().equals(u.getEmail())))){
            throw new IllegalStateException("중복되는 이름 or 이메일입니다.");
        }

        UUID newProfileId = null;
        if(req.newProfile() != null){
            BinaryContentRecord binaryContentRecord = req.newProfile();
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentRecord.contentType(),
                    binaryContentRecord.bytes()
            );
            BinaryContent saved = binaryContentRepository.save(binaryContent);
            newProfileId = saved.getId();
        }

        user.update(req.name(),
                req.email(),
                req.password(),
                newProfileId);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new IllegalStateException("존재하지 않는 id입니다.")
                );

        UUID profileID = user.getProfileID();
        if(profileID != null){
            binaryContentRepository.deleteByID(profileID);
        }

        userStatusRepository.deleteByUserID(userId);
        userRepository.deleteById(userId);
    }
}
