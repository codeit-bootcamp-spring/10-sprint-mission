package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    // 사용자 생성
    @Override
    public UserDto create(UserCreateRequestDTO userCreateRequestDTO) {
        isEmailDuplicate(userCreateRequestDTO.email());
        isUsernameDuplicate(userCreateRequestDTO.username());

        UserEntity newUser = new UserEntity(userCreateRequestDTO);
        userRepository.save(newUser);

        UserStatusEntity newUserStatus = new UserStatusEntity(newUser.getId());
        userStatusRepository.save(newUserStatus);

        Optional.ofNullable(userCreateRequestDTO.binaryContentCreateRequestDTO())
                .map(BinaryContentCreateRequestDTO:: binaryContent)
                // 선택적 프로필 이미지 생성
                .ifPresent(content -> {
                    BinaryContentEntity newBinaryContent = new BinaryContentEntity(userCreateRequestDTO.binaryContentCreateRequestDTO());
                    binaryContentRepository.save(newBinaryContent);
                    newUser.updateProfileId(newBinaryContent.getId());
                });

        return toResponseDTO(newUser, newUserStatus);
    }

    // 사용자 단건 조회
    @Override
    public UserDto findById(UUID userId) {
        UserEntity targetUser = findEntityById(userId);

        UserStatusEntity targetUserStatus = userStatusRepository.findByUserId(targetUser.getId());

        return toResponseDTO(targetUser, targetUserStatus);
    }

    // 사용자 전체 조회
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // 사용자별 상태 조회
                    UserStatusEntity userStatus = userStatusRepository.findByUserId(user.getId());
                    return toResponseDTO(user, userStatus);
                })
                .toList();
    }

    // 특정 채널의 참가자 목록 조회
    @Override
    public List<UserDto> findMembersByChannelId(MemberFindRequestDTO memberFindRequestDTO) {
        ChannelEntity targetChannel = channelRepository.findById(memberFindRequestDTO.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        // Private 채널은 채널 참여자만 조회 가능
        if (targetChannel.getType() == ChannelType.PRIVATE &&
                !targetChannel.getMembers().contains(memberFindRequestDTO.requesterId())) {
                throw new RuntimeException("비공개 채널의 멤버 목록은 해당 채널 참여자만 조회할 수 있습니다.");
        }

        return targetChannel.getMembers().stream()
                .map(memberId -> {
                    UserEntity user = findEntityById(memberId);
                    UserStatusEntity userStatus = userStatusRepository.findByUserId(user.getId());
                    return toResponseDTO(user,userStatus);
                })
                .toList();
    }

    // 사용자 정보 수정
    @Override
    public UserDto update(UUID userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        UserEntity targetUser = findEntityById(userId);

        UserStatusEntity targetUserStatus = userStatusRepository.findByUserId(targetUser.getId());

        // 비밀번호 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.password())
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), password, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        // 닉네임 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.username())
                .ifPresent(username -> {
                    validateString(username, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getUsername(), username, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateUsername(username);
                });

        // 상태 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.userStatusCreateRequestDTO())
                .map(UserStatusUpdateRequestDTO::userStatusType)
                .ifPresent(userStatusType -> {
                    targetUserStatus.updateStatus(userStatusType);
                    userStatusRepository.save(targetUserStatus);
                });

        // 프로필 이미지 변경
        Optional.ofNullable(userUpdateRequestDTO.binaryContentCreateRequestDTO())
                .map(BinaryContentCreateRequestDTO:: binaryContent)
                .map(binaryContent -> {
                    BinaryContentEntity newBinaryContent = new BinaryContentEntity(userUpdateRequestDTO.binaryContentCreateRequestDTO());
                    binaryContentRepository.save(newBinaryContent);
                    return newBinaryContent.getId();
                })
                .ifPresent(targetUser::updateProfileId);

        userRepository.save(targetUser);

        return toResponseDTO(targetUser, targetUserStatus);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID userId) {
        UserEntity targetUser = findEntityById(userId);

        // 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUserId().equals(userId))
                .toList()
                .forEach(channel -> {
                    channel.getMembers().removeIf(memberID -> memberID.equals(userId));
                    channelRepository.save(channel);
                });

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        messageRepository.findAll().stream()
                .filter(message ->  message.getAuthorId().equals(userId))
                .toList()
                .forEach(messageRepository::delete);

        // 사용자 상태 연쇄 삭제
        userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(targetUser.getId()))
                .toList()
                .forEach(userStatusRepository::delete);

        // 사용자 프로필 이미지 연쇄 삭제
        binaryContentRepository.findAll().stream()
                .filter(binaryContent -> binaryContent.getId().equals(targetUser.getProfileId()))
                .toList()
                .forEach(binaryContentRepository::delete);

        userRepository.delete(targetUser);
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 유효성 검사 (이름 중복)
    public void isUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
    }

    // 단일 엔티티 조회 및 반환
    public UserEntity findEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 엔티티 -> 응답 DTO 변환
    public UserDto toResponseDTO(UserEntity user, UserStatusEntity userStatus) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profileId(user.getProfileId())
                .online(userStatus.getStatus() == UserStatusType.ONLINE)
                .build();
    }
}