package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.userdto.UserRegitrationRecord;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserDTOMapper;
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

        User user = UserDTOMapper.regtoUser(req, binaryContentRepository);
        User savedUser = userRepository.save(user);

        userStatusRepository.save(new UserStatus(savedUser.getId()));

        return savedUser;
    }

    @Override
    public UserResponseDTO find(UUID userId) {
        return UserDTOMapper.userIdToResponse(
                userId,
                userRepository,
                userStatusRepository
        );
    }

    @Override
    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> userDTOList;
        return userRepository
                .findAll()
                .stream()
                .map(u -> {
//                    UserStatus userStatus = userStatusRepository.findByID(u.getId());
//                    boolean online = userStatus.isOnline();
//                    Instant lastActiveAt = userStatus.getLastActiveAt();
//                    return new UserResponseDTO(u.getId(),
//                            u.getProfileID(),
//                            u.getUsername(),
//                            u.getEmail(),
//                            online,
//                            lastActiveAt,
//                            u.getCreatedAt(),
//                            u.getUpdatedAt());
                    return UserDTOMapper.userIdToResponse(u.getId(),userRepository, userStatusRepository);
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
            BinaryContentDTO binaryContentDTO = req.newProfile();
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentDTO.contentType(),
                    binaryContentDTO.file()
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
