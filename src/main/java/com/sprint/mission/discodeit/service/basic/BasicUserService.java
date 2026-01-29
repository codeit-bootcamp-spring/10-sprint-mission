package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserInfoDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
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
    public UserInfoDto createUser(UserCreateDto userCreateDto) {
        // email, userName `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(userCreateDto.email(), "email");
        ValidationMethods.validateNullBlankString(userCreateDto.userName(), "userName");
        // email, userName 중복 확인
        validateDuplicateEmail(userCreateDto.email());
        validateDuplicateUserName(userCreateDto.userName());
        // nickName, password, birthday `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(userCreateDto.nickName(), "nickName");
        ValidationMethods.validateNullBlankString(userCreateDto.password(), "password");
        ValidationMethods.validateNullBlankString(userCreateDto.birthday(), "birthday");

        User user = new User(userCreateDto.email(), userCreateDto.userName(), userCreateDto.nickName(), userCreateDto.password(), userCreateDto.birthday());

        if (userCreateDto.profileImage() != null ) {
            byte[] inputBinaryContent = userCreateDto.profileImage().binaryContent();

            if (inputBinaryContent != null && inputBinaryContent.length != 0) {
                BinaryContent binaryContent = new BinaryContent(inputBinaryContent);
                binaryContentRepository.save(binaryContent);

                user.updateProfileId(binaryContent.getId());
            }
        }
        UserStatus userStatus = new UserStatus(user.getId());

        userRepository.save(user);
        userStatusRepository.save(userStatus);
        return createUserInfo(user, userStatus);
    }

    @Override
    public UserInfoDto findUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 userId에 맞는 UserStatus가 없습니다."));

        return createUserInfo(user, userStatus);
    }

    @Override
    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        ValidationMethods.validateNullBlankString(email, "email");
        ValidationMethods.validateNullBlankString(password, "password");

        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public List<UserInfoDto> findAllUsers() {
//        List<User> users = userRepository.findAll();
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        List<UserInfoDto> userInfos = new ArrayList<>();
        userStatuses.forEach(status -> {
            User user = userRepository.findById(status.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("status의 userId를 가진 유저가 존재하지 않음"));
            userInfos.add(createUserInfo(user, status));
        });
//        Map<UUID, UserStatus> userStatusMap = new HashMap<>();
//        userStatuses
//                .forEach(status -> userStatusMap.put(status.getUserId(), status));
//
//        List<UserInfoDto> userInfos = new ArrayList<>();
//        users.forEach(user -> userInfos.add(createUserInfo(user, userStatusMap.get(user.getId()))));
        return userInfos;
    }

    @Override
    public UserInfoDto updateUserInfo(UserUpdateDto userUpdateDto) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        ValidationMethods.validateId(userUpdateDto.userId());
        User user = userRepository.findById(userUpdateDto.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 유저를 찾을 수 없습니다."));

        // blank(+null) 검증
        validateBlankUpdateParameters(userUpdateDto);

        // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
        boolean binaryContentChanged = isBinaryContentChanged(userUpdateDto.profileImage(), user.getProfileId());

        // email or password or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
        validateAllInputDuplicateOrEmpty(userUpdateDto, user, binaryContentChanged);

        // 다른 사용자들의 email과 중복되는지 확인 후 email 업데이트
        if (userUpdateDto.email() != null && !user.getEmail().equals(userUpdateDto.email())) {
            validateDuplicateEmailForUpdate(userUpdateDto.userId(), userUpdateDto.email());
            user.updateEmail(userUpdateDto.email());
        }
        // 다른 사용자들의 userName과 중복되는지 확인 후 userName 업데이트
        if (userUpdateDto.userName() != null && !user.getUserName().equals(userUpdateDto.userName())) {
            validateDuplicateUserNameForUpdate(userUpdateDto.userId(), userUpdateDto.userName());
            user.updateUserName(userUpdateDto.userName());
        }

        // filter로 중복 확인 후 업데이트(중복 확인 안하면 동일한 값을 또 업데이트함)
        Optional.ofNullable(userUpdateDto.password())
                .filter(p -> !user.getPassword().equals(p)) // !false(중복 아닌 값) -> true
                .ifPresent(p -> user.updatePassword(p));
        Optional.ofNullable(userUpdateDto.nickName())
                .filter(n -> !user.getNickName().equals(n))
                .ifPresent(n -> user.updateNickName(n));
        Optional.ofNullable(userUpdateDto.birthday())
                .filter(b -> !user.getBirthday().equals(b))
                .ifPresent(b -> user.updateBirthday(b));

        UUID oldProfileId = user.getProfileId();
        if (binaryContentChanged) {
            BinaryContent newBinaryContent = new BinaryContent(userUpdateDto.profileImage().binaryContent());
            binaryContentRepository.save(newBinaryContent);

            user.updateProfileId(newBinaryContent.getId());
        }

        userRepository.save(user);

        // BinaryContent가 교체되고, profileId가 null이 아닐 때
        if (binaryContentChanged && oldProfileId != null) {
            binaryContentRepository.delete(oldProfileId);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 userId로 userStatus를 찾을 수 없습니다."));
        userStatus.updateLastOnlineTime();
        userStatusRepository.save(userStatus);

        return createUserInfo(user, userStatus);
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

    private UserInfoDto createUserInfo(User user, UserStatus userStatus) {
        return new UserInfoDto(user.getId(), user.getEmail(), user.getUserName(), user.getNickName(),
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
    private void validateBlankUpdateParameters(UserUpdateDto userUpdateDto) {
        if (userUpdateDto.email() != null) ValidationMethods.validateNullBlankString(userUpdateDto.email(), "email");
        if (userUpdateDto.password() != null) ValidationMethods.validateNullBlankString(userUpdateDto.password(), "password");
        if (userUpdateDto.userName() != null) ValidationMethods.validateNullBlankString(userUpdateDto.userName(), "userName");
        if (userUpdateDto.nickName() != null) ValidationMethods.validateNullBlankString(userUpdateDto.nickName(), "nickName");
        if (userUpdateDto.birthday() != null) ValidationMethods.validateNullBlankString(userUpdateDto.birthday(), "birthday");
        if (userUpdateDto.profileImage() != null) validateNullBlankBinaryContent(userUpdateDto.profileImage().binaryContent());
    }
    // email or password or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
    private void validateAllInputDuplicateOrEmpty(UserUpdateDto userUpdateDto, User user, boolean binaryContentChanged) {
        if ((userUpdateDto.email() == null || user.getEmail().equals(userUpdateDto.email()))
                && (userUpdateDto.password() == null || user.getPassword().equals(userUpdateDto.password()))
                && (userUpdateDto.nickName() == null || user.getNickName().equals(userUpdateDto.nickName()))
                && (userUpdateDto.userName() == null || user.getUserName().equals(userUpdateDto.userName()))
                && (userUpdateDto.birthday() == null || user.getBirthday().equals(userUpdateDto.birthday()))
                && !binaryContentChanged
        ) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }
    }
    // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
    private boolean isBinaryContentChanged (BinaryContentCreateDto binaryContentCreateDto, UUID profileId) {
        if (binaryContentCreateDto != null) { // 새 BinaryContent 들어오는데
            if (profileId == null) { // 기존에 BinaryContent 없을 때
                return true; // 새로운 BinaryContent 들어옴
            } else { //기존 프로필이 존재
                BinaryContent oldBinaryContent = binaryContentRepository.findById(profileId)
                        .orElseThrow(() -> new NoSuchElementException("해당 profileId에 해당하는 BinaryContent가 없습니다."));
                // 새로 들어온 BinaryContent와 비교
                // 같으면 -> false -> change 되지 않음
                return !Arrays.equals(oldBinaryContent.getContent(), binaryContentCreateDto.binaryContent());
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
