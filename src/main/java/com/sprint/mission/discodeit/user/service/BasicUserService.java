package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.user.User;
import com.sprint.mission.discodeit.user.exception.EmailDuplicationException;
import com.sprint.mission.discodeit.user.exception.UserDuplicationException;
import com.sprint.mission.discodeit.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.userstatus.UserStatus;
import com.sprint.mission.discodeit.user.UserMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.user.dto.UserCreateInfo;
import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.user.dto.UserInfoWithStatus;
import com.sprint.mission.discodeit.user.dto.UserUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository contentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserInfo createUser(UserCreateInfo userInfo) {
        // 유저 이름 & 이메일 검증
        validateUserExist(userInfo.userName());
        validateEmailExist(userInfo.email());

        // 유저 생성 -> mapper로 대체 가능
        User user = new User(userInfo.userName(), userInfo.password(), userInfo.email());

        // status 생성
        UserStatus status = new UserStatus(user.getId());

        // profile image가 존재한다면 생성
        BinaryContent profileImage = null;
        if(userInfo.profileImage() != null) {
            profileImage = new BinaryContent(userInfo.profileImage());
            user.setProfileId(profileImage.getId());
            contentRepository.save(profileImage);
        }

        // Repo 저장
        userStatusRepository.save(status);
        userRepository.save(user);
        return UserMapper.toUserInfo(user, status);
    }

    @Override
    public UserInfoWithStatus findUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자의 상태 정보가 존재하지 않습니다."));
        return UserMapper.toUserInfoWithStatus(user, status);
    }

    @Override
    public List<UserInfoWithStatus> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("해당 사용자의 상태 정보가 존재하지 않습니다."));
                    return UserMapper.toUserInfoWithStatus(user, status);
                })
                .toList();
    }

    @Override
    public List<UserInfoWithStatus> findAllByChannelId(UUID channelId) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getChannelIds().contains(channelId))
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("해당 사용자의 상태 정보가 존재하지 않습니다."));
                    return UserMapper.toUserInfoWithStatus(user, status);
                })
                .toList();
    }

    @Override
    public UserInfo updateUser(UUID userId, UserUpdateInfo updateInfo) {
        validateUserExist(updateInfo.userName());
        validateEmailExist(updateInfo.email());
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Optional.ofNullable(updateInfo.userName())
                .ifPresent(findUser::updateUserName);
        Optional.ofNullable(updateInfo.password())
                .ifPresent(findUser::updatePassword);
        Optional.ofNullable(updateInfo.email())
                .ifPresent(findUser::updateEmail);

        // profileId가 존재하면 업데이트
        if(updateInfo.profileId() != null) {
            if(findUser.isProfileImageUploaded())
                contentRepository.deleteById(findUser.getProfileId());
            BinaryContent newContent = new BinaryContent(updateInfo.profileImage());
            findUser.setProfileId(newContent.getId());
            contentRepository.save(newContent);
        }

        // statusRepo.findByUserId로 찾기
        UserStatus status = userStatusRepository.findByUserId(findUser.getId())
                .map(findStatus -> {
                    findStatus.updateLastOnlineAt();
                    return findStatus;
                })
                .orElseThrow(() -> new IllegalStateException("해당 사용자의 접속 정보를 찾을 수 없습니다."));
        // status 업데이트 & save
        userStatusRepository.save(status);

        userRepository.save(findUser);
        return UserMapper.toUserInfo(findUser, status);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        channelRepository.findAllByUserId(userId).forEach(channel -> {
            channel.removeUserId(userId);
            channelRepository.save(channel);
        });
        if(user.isProfileImageUploaded())
            contentRepository.deleteById(user.getProfileId());
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private void validateUserExist(String userName) {
        if(userRepository.findByName(userName).isPresent())
            throw new UserDuplicationException();
    }

    private void validateEmailExist(String email) {
        if(userRepository.findByEmail(email).isPresent())
            throw new EmailDuplicationException();
    }
}
