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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
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
    public UserDto.Response create(UserDto.CreateRequest request, MultipartFile profileImage) {
        String username = request.username();
        String email = request.email();
        validateUser(null, username, email);
        validateImageFile(profileImage);

        String password = passwordEncoder.encode(request.password());
        UUID profileId = saveProfileImage(profileImage);

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
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request, MultipartFile newProfileImage) {
        User user=  userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));


        if (request == null) {
            request = new UserDto.UpdateRequest(null, null, null);
        }

        if (request.newUsername() == null &&
                request.newEmail() == null &&
                request.newPassword() == null &&
                (newProfileImage == null || newProfileImage.isEmpty())) {
            throw new IllegalArgumentException("변경할 항목이 적어도 하나는 필요합니다.");
        }

        validateImageFile(newProfileImage); // 이미지 파일인지 검사

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        validateUser(user, newUsername, newEmail);

        String newPassword = request.newPassword();
        if(newPassword != null) newPassword = passwordEncoder.encode(newPassword);

        UUID newProfileId = saveProfileImage(newProfileImage);
        if (newProfileId != null) { // 파일이 있다면
            if (user.getProfileId() != null) { // 기존 유저 프로필 파일 삭제
                binaryContentRepository.deleteById(user.getProfileId());
            }
        } else { // 파일이 없다면
            newProfileId = user.getProfileId(); // 기존 유저 프로필 유지
        }

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(newUsername, newEmail, newPassword, newProfileId);
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

    private UUID saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            BinaryContent content = new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes() // IOException 발생 가능
            );
            return binaryContentRepository.save(content).getId();
        } catch (IOException e) {
            throw new UncheckedIOException("프로필 이미지 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        // ContentType이 null이거나 image/로 시작하지 않는 경우 차단
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일(jpg, png, gif 등)만 업로드 가능합니다.");
        }
    }
}
