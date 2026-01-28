package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public UserDTO.Response create(UserDTO.Create request) {
        existsByUsername(request.username());
        existsByEmail(request.email());
        User user = new User(request.username(), request.email(), request.password());
        userRepository.save(user);

        //요청에 프로필이 있다면 binaryContent 객체 생성 후 저장
        if (request.binaryContent() != null) {
            BinaryContentDTO.Create profileDto = request.binaryContent();

            BinaryContent profile = new BinaryContent(
                    profileDto.fileName(),
                    profileDto.bytes()
            );

            binaryContentRepository.save(profile);

            user.updateProfileId(profile.getId());
            userRepository.save(user);
        }

        //유저 상태 객체 생성 후 저장
        UserStatus status = new UserStatus(user.getId());
        status.updateOnline();

        userStatusRepository.save(status);

        return UserDTO.Response.of(user, status);
    }

    @Override
    public UserDTO.Response findById(UUID userId) {
        User user = userRepository.findById(userId);
        UserStatus status = userStatusRepository.findByUserId(userId);
        return UserDTO.Response.of(user, status);
    }

    @Override
    public List<UserDTO.Response> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId());
                    return UserDTO.Response.of(user, status);
                })
                .toList();
    }

    @Override
    public UserDTO.Response update(UUID userId, UserDTO.Update request) {
        User user = userRepository.findById(userId);

        Optional.ofNullable(request.username()).ifPresent(user::updateUsername);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        Optional.ofNullable(request.password()).ifPresent(user::updatePassword);

        if (request.binaryContent() != null) {
            if (user.getProfileId() != null) {
                BinaryContent oldProfile = binaryContentRepository.findById(user.getProfileId());
                binaryContentRepository.delete(oldProfile);
            }

            BinaryContentDTO.Create profileDto = request.binaryContent();

            BinaryContent newProfile = new BinaryContent(
                    profileDto.fileName(),
                    profileDto.bytes()
            );
            binaryContentRepository.save(newProfile);

            user.updateProfileId(newProfile.getId());
        }
        userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(userId);
        status.updateOnline();

        userStatusRepository.save(status);
        return UserDTO.Response.of(user, status);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId);

        //유저 탈퇴시 유저 메세지 지우기
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
        for (Message message : messages) {
            messageRepository.delete(message);
        }

        //유저 탈퇴 시 채널에서 탈퇴
        for (UUID channelId : user.getChannelIds()) {
            Channel channel = channelRepository.findById(channelId);
            channel.deleteUser(userId);
            channelRepository.save(channel);
        }

        //유저 상태 삭제
        UserStatus status = userStatusRepository.findByUserId(userId);
        userStatusRepository.delete(status);

        //유저 프로필 삭제
        if (user.getProfileId() != null) {
            BinaryContent profile = binaryContentRepository.findById(user.getProfileId());
            binaryContentRepository.delete(profile);
        }

        userRepository.delete(user);
    }

    //유저명 중복체크
    private void existsByUsername(String username) {
        boolean exist = userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 유저 이름입니다: " + username);
        }
    }

    //유저 이메일 중복체크
    private void existsByEmail(String email) {
        boolean exist = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다: " + email);
        }
    }
}
