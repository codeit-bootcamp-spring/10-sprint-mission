package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.input.BinaryContentCreateInput;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserWithOnlineResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateInput;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User createUser(UserCreateRequest request) {
        // email, username 중복 확인
        validateDuplicateEmail(request.email());
        validateDuplicateUserName(request.username());

        User user = new User(request.email(), request.username(), request.nickName(), request.password(), request.birthday());

        if (request.profileImage() != null ) {
            byte[] inputBinaryContent = request.profileImage().bytes();
            String contentType = request.profileImage().contentType();

            if (inputBinaryContent != null && inputBinaryContent.length != 0) {
                BinaryContent binaryContent = new BinaryContent(contentType, inputBinaryContent);
                binaryContentRepository.save(binaryContent);

                user.updateProfileId(binaryContent.getId());
            }
        }
        UserStatus userStatus = new UserStatus(user.getId());

        userRepository.save(user);
        userStatusRepository.save(userStatus);
        return user;
    }

    @Override
    public UserWithOnlineResponse findUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId에 맞는 UserStatus가 없습니다."));

        return createUserWithOnlineResponse(user, userStatus);
    }

    @Override
    public List<UserWithOnlineResponse> findAllUsers() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        List<UserWithOnlineResponse> userInfos = new ArrayList<>();
        userStatuses.forEach(status -> {
            User user = userRepository.findById(status.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("status의 userId를 가진 유저가 존재하지 않음"));
            userInfos.add(createUserWithOnlineResponse(user, status));
        });

        return userInfos;
    }

    @Override
    public User updateUserInfo(@Valid UserUpdateInput input) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));

        // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
        boolean binaryContentChanged = isBinaryContentChanged(input.profileImage(), user.getProfileId());

        // email or password or username 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
        validateAllInputDuplicateOrEmpty(input, user, binaryContentChanged);

        // 다른 사용자들의 email과 중복되는지 확인 후 email 업데이트
        if (input.email() != null && !user.getEmail().equals(input.email())) {
            validateDuplicateEmailForUpdate(input.userId(), input.email());
            user.updateEmail(input.email());
        }
        // 다른 사용자들의 userName과 중복되는지 확인 후 username 업데이트
        if (input.username() != null && !user.getUsername().equals(input.username())) {
            validateDuplicateUserNameForUpdate(input.userId(), input.username());
            user.updateUserName(input.username());
        }

        // filter로 중복 확인 후 업데이트(중복 확인 안하면 동일한 값을 또 업데이트함)
        Optional.ofNullable(input.password())
                .filter(p -> !user.getPassword().equals(p)) // !false(중복 아닌 값) -> true
                .ifPresent(p -> user.updatePassword(p));
        Optional.ofNullable(input.nickName())
                .filter(n -> !user.getNickName().equals(n))
                .ifPresent(n -> user.updateNickName(n));
        Optional.ofNullable(input.birthday())
                .filter(b -> !user.getBirthday().equals(b))
                .ifPresent(b -> user.updateBirthday(b));

        UUID oldProfileId = user.getProfileId();
        if (binaryContentChanged) {
            byte[] inputBinaryContent = input.profileImage().bytes();
            String contentType = input.profileImage().contentType();
            BinaryContent newBinaryContent = new BinaryContent(contentType, inputBinaryContent);
            binaryContentRepository.save(newBinaryContent);

            user.updateProfileId(newBinaryContent.getId());
        }

        userRepository.save(user);

        // BinaryContent가 교체되고, profileId가 null이 아닐 때
        if (binaryContentChanged && oldProfileId != null) {
            binaryContentRepository.delete(oldProfileId);
        }

        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 userStatus를 찾을 수 없습니다."));

        // channel, message 삭제는 상위에서
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }
        userStatusRepository.delete(userStatus.getId());
        userRepository.delete(userId);
    }

    private UserWithOnlineResponse createUserWithOnlineResponse(User user, UserStatus userStatus) {
        return new UserWithOnlineResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getEmail(), user.getUsername(), user.getNickName(),
                user.getBirthday(), user.getProfileId(), userStatus.isOnlineStatus());
    }

    //// validation
    // user ID null & user 객체 존재 확인
    public User validateAndGetUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    // email이 이미 존재하는지 확인
    private void validateDuplicateEmail(String newEmail) {
        if (userRepository.existEmail(newEmail)) {
            throw new IllegalArgumentException("동일한 email이 존재합니다");
        }
    }
    // userName이 이미 존재하는지 확인
    private void validateDuplicateUserName(String newUserName) {
        if (userRepository.existUserName(newUserName)) {
            throw new IllegalArgumentException("동일한 userName이 존재합니다");
        }
    }
    // email or password or username 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
    private void validateAllInputDuplicateOrEmpty(UserUpdateInput input, User user, boolean binaryContentChanged) {
        if ((input.email() == null || user.getEmail().equals(input.email()))
                && (input.password() == null || user.getPassword().equals(input.password()))
                && (input.nickName() == null || user.getNickName().equals(input.nickName()))
                && (input.username() == null || user.getUsername().equals(input.username()))
                && (input.birthday() == null || user.getBirthday().equals(input.birthday()))
                && !binaryContentChanged
        ) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }
    }
    // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
    private boolean isBinaryContentChanged (BinaryContentCreateInput binaryContentCreateInput, UUID profileId) {
        if (binaryContentCreateInput != null) { // 새 BinaryContent 들어오는데
            if (profileId == null) { // 기존에 BinaryContent 없을 때
                return true; // 새로운 BinaryContent 들어옴
            } else { //기존 프로필이 존재
                BinaryContent oldBinaryContent = binaryContentRepository.findById(profileId)
                        .orElseThrow(() -> new NoSuchElementException("해당 profileId에 해당하는 BinaryContent가 없습니다."));
                // 새로 들어온 BinaryContent와 비교
                // 같으면 -> false -> change 되지 않음
                return !Arrays.equals(oldBinaryContent.getBytes(), binaryContentCreateInput.bytes());
            }
        }
        return false; // 새 BinaryContent 안들어옴
    }
    // binaryContent가 null 인지, 없는지 확인
    public static void validateNullBlankBinaryContent(byte[] binaryContent) {
        if (binaryContent == null || binaryContent.length == 0) {
            throw new IllegalArgumentException("binaryContent가 입력되지 않았습니다.");
        }
    }
    // 나를 제외한 email 중에 중복된 값이 있는지 확인
    private void validateDuplicateEmailForUpdate(UUID userId, String newEmail) {
        if (userRepository.isEmailUsedByOther(userId, newEmail)) {
            throw new IllegalArgumentException("동일한 email이 존재합니다");
        }
    }
    // 나를 제외한 username 중에 중복된 값이 있는지 확인
    private void validateDuplicateUserNameForUpdate(UUID userId, String newUserName) {
        if (userRepository.isUserNameUsedByOther(userId, newUserName)) {
            throw new IllegalArgumentException("동일한 userName이 존재합니다");
        }
    }
}
