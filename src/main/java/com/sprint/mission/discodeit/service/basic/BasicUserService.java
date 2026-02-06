package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto.Response create(UserDto.CreateRequest request) {
        String username = request.username();
        String email = request.email();
        validateUser(null, username, email);

        String password = passwordEncoder.encode(request.password());
        UUID profileId = saveProfileImage(request.profileImage());

        User user = new User(username, email, password, profileId);
        User createdUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(createdUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);
        return toDto(createdUser);
    }

    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request) {
        User user=  userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        validateUser(user, newUsername, newEmail);

        String newPassword = request.newPassword();
        if(newPassword != null) newPassword = passwordEncoder.encode(newPassword);

        UUID newProfileId = saveProfileImage(request.newProfileImage());
        UUID oldProfileId = user.getProfileId();

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(newUsername, newEmail, newPassword, newProfileId);

        // 기존 프로필 삭제는 실패해도 유저 업데이트와는 별개로 성공해야 하므로 개선 여지 있음
        if (oldProfileId != null && !oldProfileId.equals(newProfileId)) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        User updatedUser = userRepository.save(user);

        return toDto(updatedUser);
    }

    // 트랜잭션은 aggregate 단위로만 묶는다.
    // 현재 user는 userStatus없이 생존할 수 있으므로 트랜잭션이 부적절할 수 있다.
    // @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    // validation
    private void validateUser(User user, String username, String email) {
        // 유저가 null이면 create, 있으면 update
        // 유저가 null이 아닌 경우만(update면) 값이 변경되었는지 equal로 체크해서 변경되지 않았으면 스킵
        if (email != null
                && userRepository.existsByEmail(email)
                && (user == null || !user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (username != null
                && userRepository.existsByUsername(username)
                && (user == null || !user.getUsername().equals(username))) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }
    }

    // Helper
    public UserDto.Response toDto(User user) {
        // 현재는 요구사항에서 find, findAll이 유저 상태 정보를 같이 포함하라고 해서 강하게 결합했음
        // 유저 상태 정보가 손실되어도 유저는 정상적으로 작동해야 하므로 실제로는 예외 처리를 하면 안됨
        // 트랜잭션으로 정합성을 보장하거나, 손실되면 offline으로 유연하게 처리하도록 바꿔야 할 필요성 존재
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저의 상태가 없습니다:" + user.getId()));

        return userMapper.toResponse(user, userStatus.isOnline());
    }

    private UUID saveProfileImage(BinaryContentDto.CreateRequest imageRequest) {
        if (imageRequest == null) {
            return null;
        }

        BinaryContent content = new BinaryContent(
                imageRequest.fileName(),
                imageRequest.contentType(),
                imageRequest.content()
        );

        return binaryContentRepository.save(content).getId();
    }
}
