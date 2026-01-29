package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()) ||
                userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("중복된 사용자 정보가 존재합니다.");
        }

        String profileId = null;
        BinaryContentDto imageDto = dto.getProfileImage();

        if (imageDto != null && imageDto.getBytes() != null) {
            BinaryContent content = new BinaryContent(
                    imageDto.getBytes(),
                    imageDto.getFileName(),
                    imageDto.getContentType()
            );
            binaryContentRepository.save(content);
            profileId = content.getId();
        }

        User user = new User(dto.getUsername(), dto.getEmail(), dto.getPassword());
        user.updateProfileId(profileId);
        userRepository.save(user);

        userStatusRepository.save(new UserStatus(user.getId()));

        return new UserResponseDto(user, false);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(user, isOnline);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserResponseDto(user, isOnline);
                })
                .toList();
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        user.update(dto.getNickname(), null, dto.getPassword());

        BinaryContentDto imageDto = dto.getProfileImage();

        if (imageDto != null && imageDto.getBytes() != null) {
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
            BinaryContent newContent = new BinaryContent(
                    imageDto.getBytes(),
                    imageDto.getFileName(),
                    imageDto.getContentType()
            );
            binaryContentRepository.save(newContent);
            user.updateProfileId(newContent.getId());
        }

        userRepository.save(user);

        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(user, isOnline);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}