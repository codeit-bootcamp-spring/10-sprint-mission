package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserFindingDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequestDto userCreateRequestDto) {
        //중복여부 검사 로직
        if (findAll().stream()
                .anyMatch(user ->
                        user.username().equals(userCreateRequestDto.username()) ||
                                user.email().equals(userCreateRequestDto.email())
                )) throw new AssertionError("Username or Email already exists");

        User user;
        //이미지 존재여부 분기
        if(userCreateRequestDto.profileImage() != null){
            BinaryContent profileImage = new BinaryContent(userCreateRequestDto.profileImage().content().get(0));
            binaryContentRepository.save(profileImage);

            user = new User(userCreateRequestDto.username(),
                    userCreateRequestDto.email(),
                    userCreateRequestDto.password(),
                    profileImage.getId());
        }
        else user = new User(userCreateRequestDto.username(),
                userCreateRequestDto.email(),
                userCreateRequestDto.password(),
                null);

        //userstatus 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    @Override
    public UserFindingDto find(UUID userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus targetUserStatus = userStatusRepository.findById(targetUser.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + targetUser.getId() + " not found"));

        return new UserFindingDto(
                targetUser.getUsername(),
                targetUser.getEmail(),
                targetUserStatus.isOnline()
        );
    }

    @Override
    public List<UserFindingDto> findAll() {
        List<User> targetUsers = userRepository.findAll();

        return targetUsers.stream()
                .map(user -> find(user.getId()))
                .toList();
    }

    @Override
    public User update(UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findById(userUpdateRequestDto.targetUserId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userUpdateRequestDto.targetUserId() + " not found"));

        BinaryContent newProfileImage;

        if(userUpdateRequestDto.profileImage() != null){
            newProfileImage = new BinaryContent(userUpdateRequestDto.profileImage().content().get(0));

            if(user.getProfileId() != null) binaryContentRepository.deleteById(user.getProfileId());
            binaryContentRepository.save(newProfileImage);

        }
        else{
            newProfileImage = null;

        }

        boolean anyValueUpdated = false;
        if (userUpdateRequestDto.newUsername() != null && !userUpdateRequestDto.newUsername().equals(user.getUsername())) {
            user.setUsername(userUpdateRequestDto.newUsername());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newEmail() != null && !userUpdateRequestDto.newEmail().equals(user.getEmail())) {
            user.setEmail(userUpdateRequestDto.newEmail());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newPassword() != null && !userUpdateRequestDto.newPassword().equals(user.getPassword())) {
            user.setPassword(userUpdateRequestDto.newPassword());
            anyValueUpdated = true;
        }
        if (newProfileImage != null && !Arrays.equals(binaryContentRepository
                .findById(user.getProfileId())
                .orElseThrow()
                .getContent(), userUpdateRequestDto.profileImage().content().get(0)))
        {
            binaryContentRepository.deleteById(user.getProfileId());
            user.setProfileId(newProfileImage.getId());
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            user.isUpdated();
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        //유저가 검색되지 않는 경우
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        User deletedUser = userRepository.findById(userId).get();
        binaryContentRepository.deleteById(deletedUser.getProfileId());//프로필 이미지 삭제

        UserStatus userStatus = userStatusRepository.findByUserId(userId).get();
        userStatusRepository.deleteById(userStatus.getId());//스테이터스 삭제

        userRepository.deleteById(userId);//유저레포지토리에서 삭제
    }
}
