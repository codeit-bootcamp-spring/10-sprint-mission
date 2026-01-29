package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User createUser(UserCreateRequest request) {
        // email, userName `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(request.email(), "email");
        ValidationMethods.validateNullBlankString(request.userName(), "userName");
        // email, userName 중복 확인
        validateDuplicateEmail(request.email());
        validateDuplicateUserName(request.userName());
        // nickName, password, birthday `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(request.nickName(), "nickName");
        ValidationMethods.validateNullBlankString(request.password(), "password");
        ValidationMethods.validateNullBlankString(request.birthday(), "birthday");

        User user = new User(request.email(), request.userName(), request.nickName(), request.password(), request.birthday());

        if (request.profileImage() != null ) {
            byte[] inputBinaryContent = request.profileImage().binaryContent();

            if (inputBinaryContent != null && inputBinaryContent.length != 0) {
                BinaryContent binaryContent = new BinaryContent(inputBinaryContent);
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
    public UserResponse findUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId에 맞는 UserStatus가 없습니다."));

        return createUserResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAllUsers() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        List<UserResponse> userInfos = new ArrayList<>();
        userStatuses.forEach(status -> {
            User user = userRepository.findById(status.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("status의 userId를 가진 유저가 존재하지 않음"));
            userInfos.add(createUserResponse(user, status));
        });

//        userRepository.findAll().stream()
//                .map(user -> findUserById(user.getId()))
//                .toList();

        return userInfos;
    }

    @Override
    public User updateUserInfo(UserUpdateRequest request) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        ValidationMethods.validateId(request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 유저를 찾을 수 없습니다."));

        // blank(+null) 검증
        validateBlankUpdateParameters(request);

        // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
        boolean binaryContentChanged = isBinaryContentChanged(request.profileImage(), user.getProfileId());

        // email or password or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
        validateAllInputDuplicateOrEmpty(request, user, binaryContentChanged);

        // 다른 사용자들의 email과 중복되는지 확인 후 email 업데이트
        if (request.email() != null && !user.getEmail().equals(request.email())) {
            validateDuplicateEmailForUpdate(request.userId(), request.email());
            user.updateEmail(request.email());
        }
        // 다른 사용자들의 userName과 중복되는지 확인 후 userName 업데이트
        if (request.userName() != null && !user.getUserName().equals(request.userName())) {
            validateDuplicateUserNameForUpdate(request.userId(), request.userName());
            user.updateUserName(request.userName());
        }

        // filter로 중복 확인 후 업데이트(중복 확인 안하면 동일한 값을 또 업데이트함)
        Optional.ofNullable(request.password())
                .filter(p -> !user.getPassword().equals(p)) // !false(중복 아닌 값) -> true
                .ifPresent(p -> user.updatePassword(p));
        Optional.ofNullable(request.nickName())
                .filter(n -> !user.getNickName().equals(n))
                .ifPresent(n -> user.updateNickName(n));
        Optional.ofNullable(request.birthday())
                .filter(b -> !user.getBirthday().equals(b))
                .ifPresent(b -> user.updateBirthday(b));

        UUID oldProfileId = user.getProfileId();
        if (binaryContentChanged) {
            BinaryContent newBinaryContent = new BinaryContent(request.profileImage().binaryContent());
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
        ValidationMethods.validateId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 유저를 찾을 수 없습니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 userStatus를 찾을 수 없습니다."));

        // channel, message 삭제는 상위에서
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }
        userStatusRepository.delete(userStatus.getId());
        userRepository.delete(userId);
    }

    private UserResponse createUserResponse(User user, UserStatus userStatus) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUserName(), user.getNickName(),
                user.getBirthday(), user.getProfileId(), userStatus.isOnlineStatus());
    }

    //// validation
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
    // blank(+null) 검증
    private void validateBlankUpdateParameters(UserUpdateRequest request) {
        if (request.email() != null) ValidationMethods.validateNullBlankString(request.email(), "email");
        if (request.password() != null) ValidationMethods.validateNullBlankString(request.password(), "password");
        if (request.userName() != null) ValidationMethods.validateNullBlankString(request.userName(), "userName");
        if (request.nickName() != null) ValidationMethods.validateNullBlankString(request.nickName(), "nickName");
        if (request.birthday() != null) ValidationMethods.validateNullBlankString(request.birthday(), "birthday");
        if (request.profileImage() != null) validateNullBlankBinaryContent(request.profileImage().binaryContent());
    }
    // email or password or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
    private void validateAllInputDuplicateOrEmpty(UserUpdateRequest request, User user, boolean binaryContentChanged) {
        if ((request.email() == null || user.getEmail().equals(request.email()))
                && (request.password() == null || user.getPassword().equals(request.password()))
                && (request.nickName() == null || user.getNickName().equals(request.nickName()))
                && (request.userName() == null || user.getUserName().equals(request.userName()))
                && (request.birthday() == null || user.getBirthday().equals(request.birthday()))
                && !binaryContentChanged
        ) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }
    }
    // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
    private boolean isBinaryContentChanged (BinaryContentCreateRequest binaryContentCreateRequest, UUID profileId) {
        if (binaryContentCreateRequest != null) { // 새 BinaryContent 들어오는데
            if (profileId == null) { // 기존에 BinaryContent 없을 때
                return true; // 새로운 BinaryContent 들어옴
            } else { //기존 프로필이 존재
                BinaryContent oldBinaryContent = binaryContentRepository.findById(profileId)
                        .orElseThrow(() -> new NoSuchElementException("해당 profileId에 해당하는 BinaryContent가 없습니다."));
                // 새로 들어온 BinaryContent와 비교
                // 같으면 -> false -> change 되지 않음
                return !Arrays.equals(oldBinaryContent.getContent(), binaryContentCreateRequest.binaryContent());
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
    // 나를 제외한 userName 중에 중복된 값이 있는지 확인
    private void validateDuplicateUserNameForUpdate(UUID userId, String newUserName) {
        if (userRepository.isUserNameUsedByOther(userId, newUserName)) {
            throw new IllegalArgumentException("동일한 userName이 존재합니다");
        }
    }
}
