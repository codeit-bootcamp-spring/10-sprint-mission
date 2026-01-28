package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.UserFindingDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
            BinaryContent profileImage = new BinaryContent(userCreateRequestDto.profileImage().content());
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

        if(userUpdateRequestDto.profileImage() == null){
            user.update(
                    userUpdateRequestDto.newUsername(),
                    userUpdateRequestDto.newEmail(),
                    userUpdateRequestDto.newPassword(),
                    null
                    );
        }
        else{
            BinaryContent profileImage = new BinaryContent(userUpdateRequestDto.profileImage().content());

            if(user.getProfileId() != null) binaryContentRepository.deleteById(user.getProfileId());
            binaryContentRepository.save(profileImage);

            user.update(
                    userUpdateRequestDto.newUsername(),
                    userUpdateRequestDto.newEmail(),
                    userUpdateRequestDto.newPassword(),
                    profileImage.getId()
            );
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
