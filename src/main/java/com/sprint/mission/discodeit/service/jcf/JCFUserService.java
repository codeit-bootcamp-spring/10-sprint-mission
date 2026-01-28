package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFUserService implements UserService {
//    private final Map<UUID, User> data; // 이제 데이터를 userRepository에 저장하므로 , 미사용
    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // C. 생성: User 생성 후 User 객체 반환
    @Override
    public User createUser(String email, String userName, String nickName, String password, String birthday) {
        // email `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(email, "email");
        // email 중복 확인
        validateDuplicateEmail(email);
        // userName, userName, password, birthday `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(userName, "userName");
        ValidationMethods.validateNullBlankString(nickName, "nickName");
        ValidationMethods.validateNullBlankString(password, "password");
        ValidationMethods.validateNullBlankString(birthday, "birthday");

        User user = new User(email, userName, nickName, password, birthday);
        userRepository.save(user);
        return user;
    }

    // R. 읽기
    @Override
    public User findUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateId(userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // 로그인?
    @Override
    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        ValidationMethods.validateNullBlankString(email, "email");
        ValidationMethods.validateNullBlankString(password, "password");

        return userRepository.findByEmailAndPassword(email, password);
    }

    // R. 모두 읽기
    // 모든 사용자
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // U. 수정
    // 로그인 정보를 가져온다고 가정하면 `requestUserId` 와 `targetUserId` 로 나눌 필요는 없음
    // email, password, userName, nickName, birthday 수정
    @Override
    public User updateUserInfo(UUID userId, String email, String password, String userName, String nickName, String birthday) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = findUserById(userId);
        // blank 검증
        if (email != null) ValidationMethods.validateNullBlankString(email, "email");
        if (password != null) ValidationMethods.validateNullBlankString(password, "password");
        if (userName != null) ValidationMethods.validateNullBlankString(userName, "userName");
        if (nickName != null) ValidationMethods.validateNullBlankString(nickName, "nickName");
        if (birthday != null) ValidationMethods.validateNullBlankString(birthday, "birthday");
        // email or password or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
        if ((email == null || user.getEmail().equals(email))
                && (password == null || user.getPassword().equals(password))
                && (nickName == null || user.getNickName().equals(nickName))
                && (userName == null || user.getUserName().equals(userName))
                && (birthday == null || user.getBirthday().equals(birthday))) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }

        // 다른 사용자들의 이메일과 중복되는지 확인 후 이메일 업데이트
        if (email != null && !user.getEmail().equals(email)) {
            validateDuplicateEmailForUpdate(userId, email);
            user.updateEmail(email);
        }

        // filter로 중복 확인 후 업데이트(중복 확인 안하면 동일한 값을 또 업데이트함)
        Optional.ofNullable(password)
                .filter(p -> !user.getPassword().equals(p)) // !false(중복 아닌 값) -> true
                .ifPresent(p -> user.updatePassword(p));
        Optional.ofNullable(nickName)
                .filter(n -> !user.getNickName().equals(n))
                .ifPresent(n -> user.updateNickName(n));
        Optional.ofNullable(userName)
                .filter(n -> !user.getUserName().equals(n))
                .ifPresent(n -> user.updateUserName(n));
        Optional.ofNullable(birthday)
                .filter(b -> !user.getBirthday().equals(b))
                .ifPresent(b -> user.updateBirthday(b));
        userRepository.save(user);
        return user;
    }

    // D. 삭제
    @Override
    public void deleteUser(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        findUserById(userId);

        // channel, message 삭제는 상위에서
        userRepository.delete(userId);
    }

    //// validation
    // email이 이미 존재하는지 확인
    private void validateDuplicateEmail(String newEmail) {
        if (userRepository.existByEmail(newEmail)) {
            throw new IllegalArgumentException("동일한 email이 존재합니다");
        }
    }
    // 나를 제외한 이메일 중에 중복된 값이 있는지 확인
    private void validateDuplicateEmailForUpdate(UUID userId, String newEmail) {
        if (userRepository.isEmailUsedByOther(userId, newEmail)) {
            throw new IllegalArgumentException("동일한 email이 존재합니다");
        }
    }
}
