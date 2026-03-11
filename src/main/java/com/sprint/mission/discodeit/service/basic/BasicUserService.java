package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentService binaryContentService;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest request,
                          BinaryContentCreateRequest profileDto) {

        BinaryContent profile = (profileDto == null) ? null :
                binaryContentService.create(profileDto);

        binaryContentRepository.save(profile);

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profile);

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user, Instant.now());
        userStatusRepository.save(userStatus);


        return userMapper.toDto(user, true);
    }



    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        User user = getUser(userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        boolean online = isOnline(userStatus.getLastActiveAt());

        return userMapper.toDto(user, online);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();

        // [N+1 문제 해결] 모든 유저 ID를 추출해서 한 번의 쿼리로 상태를 가져옴
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // (주의: UserStatusRepository에 findAllByUserIdIn(List<UUID>) 메서드 추가 필요)
        Map<UUID, UserStatus> statusMap = userStatusRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(UserStatus::getUserId, us -> us));

        return users.stream()
                .map(user -> {
                    UserStatus status = statusMap.get(user.getId());
                    boolean online = status != null && isOnline(status.getLastActiveAt());
                    return userMapper.toDto(user, online);
                })
                .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          BinaryContentCreateRequest profileRequest) {
        User user = getUser(userId);

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        BinaryContent newProfile = user.getProfile();
        if (profileRequest != null) {
            if (newProfile != null) {
                binaryContentService.delete(newProfile.getId());
            }
            newProfile = binaryContentService.create(profileRequest);
        }

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, newProfile);

        User savedUser = userRepository.save(user);

        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(us -> isOnline(us.getLastActiveAt()))
                .orElse(false);

        return userMapper.toDto(savedUser, isOnline);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfile())
                .ifPresent(profile -> binaryContentRepository.deleteById(profile.getId()));

        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private boolean isOnline(Instant lastOnlineAt){
        return lastOnlineAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }

    private User getUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 사용자입니다."));
    }

}
