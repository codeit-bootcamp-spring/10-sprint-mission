package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.dto.user.*;
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
    public UserInfoWithProfile createUser(UserCreateInfo userInfo) {
        // 유저 이름 & 이메일 검증
        validateUserExist(userInfo.userName());
        validateEmailExist(userInfo.email());

        // 유저 생성 -> mapper로 대체 가능
        User user = new User(userInfo.userName(), userInfo.password(), userInfo.email());

        // status 생성
        UserStatus status = new UserStatus(user.getId());

        // profile image가 존재한다면 생성
        BinaryContent profileImage = null;
        if(userInfo.profileImage() != null)
            profileImage = new BinaryContent(userInfo.profileImage());

        // 반환 Dto 만들기
        UserInfoWithProfile response = new UserInfoWithProfile(user.getUserName(), user.getEmail(),
                profileImage == null ? null : profileImage.getId());

        // Repo 저장
        userStatusRepository.save(status);
        contentRepository.save(profileImage);
        userRepository.save(user);
        return response;
    }

    @Override
    public UserInfoWithStatus getUser(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(userId)
                            .orElseThrow(() -> new NoSuchElementException("해당 사용자의 상태 정보를 찾을 수 없습니다."));
                    return new UserInfoWithStatus(user.getUserName(), user.getEmail(), status.isOnline());
                })
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public List<UserInfoWithStatus> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("해당 사용자의 상태 정보를 찾을 수 없습니다."));
                    return new UserInfoWithStatus(user.getUserName(), user.getEmail(), status.isOnline());
                })
                .toList();
    }

    @Override
    public List<UserInfo> getUsersByChannelId(UUID channelId) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getChannelIds().contains(channelId))
                .map(user -> new UserInfo(user.getUserName(), user.getEmail()))
                .toList();
    }

    @Override
    public UserInfo updateUser(UserUpdateInfo updateInfo) {
        validateUserExist(updateInfo.userName());
        validateEmailExist(updateInfo.email());
        User findUser = userRepository.findByName(updateInfo.userName())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
        Optional.ofNullable(updateInfo.userName())
                .ifPresent(findUser::updateUserName);
        Optional.ofNullable(updateInfo.password())
                .ifPresent(findUser::updatePassword);
        Optional.ofNullable(updateInfo.email())
                .ifPresent(findUser::updateEmail);

        // profileId가 존재하면 업데이트
        if(updateInfo.profileId() != null) {
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
        return new UserInfo(findUser.getUserName(), findUser.getEmail());
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
        channelRepository.findAllByUserId(userId).forEach(channel -> {
            channel.removeUserId(userId);
            channelRepository.save(channel);
        });
        contentRepository.deleteById(user.getProfileId());
        if(user.isProfileImageUploaded())
            userRepository.deleteById(userId);
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private void validateUserExist(String userName) {
        if(userRepository.findByName(userName).isPresent())
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
    }

    private void validateEmailExist(String email) {
        if(userRepository.findByEmail(email).isPresent())
            throw new IllegalStateException("이미 가입된 이메일입니다.");
    }
}
