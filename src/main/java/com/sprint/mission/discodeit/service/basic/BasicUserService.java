package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserResponseDTO;
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
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        // 1. 유효성 검증 (이메일 중복, 이름 중복)
        isEmailDuplicate(userCreateRequestDTO.getEmail());
        isNicknameDuplicate(userCreateRequestDTO.getNickname());

        // 2. User 생성, UserStatus 같이 생성
        User newUser = new User(userCreateRequestDTO);
        userRepository.save(newUser);

        UserStatus newUserStatus = new UserStatus(userCreateRequestDTO.getUserStatusCreateRequestDTO());
        userStatusRepository.save(newUserStatus);

        // 3. 선택적 프로필 이미지 생성 및 저장
        Optional.ofNullable(userCreateRequestDTO.getBinaryContentCreateRequestDTO().getBinaryContent())
                .map(content -> {
                    BinaryContent newBinaryContent = new BinaryContent(userCreateRequestDTO.getBinaryContentCreateRequestDTO());
                    binaryContentRepository.save(newBinaryContent);
                    return newBinaryContent.getId();
                })
                .ifPresent(newUser::updateProfileId);

        // 5. 응답 DTO 객체 생성 및 반환
        return toUserResponseDTO(newUser, newUserStatus);
    }

    // 사용자 단건 조회
    @Override
    public UserResponseDTO searchUser(UUID userId) {
        // 1. 사용자 존재 여부 확인
        User targetUser = findUserEntityById(userId);

        // 2. 사용자 상태 정보 조회
        UserStatus targetUserStatus = userStatusRepository.findById(targetUser.getId());

        // 2. 조회 응답 DTO 생성 및 반환
        return toUserResponseDTO(targetUser, targetUserStatus);
    }

    // 사용자 전체 조회
    @Override
    public List<UserResponseDTO> searchUserAll() {
        // 1. 조회 응답 DTO 생성 및 반환
        return userRepository.findAll().stream()
                .map(user -> {
                    // 사용자 상태 조회
                    UserStatus userStatus = userStatusRepository.findById(user.getId());
                    // 사용자 -> 사용자 조회 응답 변환
                    return toUserResponseDTO(user, userStatus);
                })
                .toList();
    }

    // 특정 채널의 참가자 목록 조회
    @Override
    public List<UserResponseDTO> searchMembersByChannelId(UUID channelId) {
        // 1. 채널 존재 여부 확인
        Channel targetChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        // 2. 특정 채널의 참가자 목록 조회
        return targetChannel.getMembers().stream()
                .map(memberId -> {
                    User user = findUserEntityById(memberId);
                    UserStatus userStatus = userStatusRepository.findById(user.getId());
                    return toUserResponseDTO(user,userStatus);
                })
                .toList();
    }

    // 사용자 정보 수정
    @Override
    public UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO) {
        // 1. 사용자 존재 여부 확인
        User targetUser = findUserEntityById(userUpdateRequestDTO.getId());

        // 2. 사용자 상태 조회
        UserStatus targetUserStatus = userStatusRepository.findById(targetUser.getId());

        // 3. 비밀번호 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.getPassword())
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), password, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        // 닉네임 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.getNickname())
                .ifPresent(nickname -> {
                    validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getNickname(), nickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateNickname(nickname);
                });
        // 상태 필드 변경
        Optional.ofNullable(userUpdateRequestDTO.getUserStatusCreateRequestDTO())
                .map(UserStatusUpdateRequestDTO::getUserStatusType)
                .ifPresent(userStatusType -> {
                    targetUserStatus.updateStatus(userStatusType);
                    userStatusRepository.save(targetUserStatus);
                });

        // 프로필 이미지 변경
        Optional.ofNullable(userUpdateRequestDTO.getBinaryContentCreateRequestDTO())
                .map(BinaryContentCreateRequestDTO::getBinaryContent)
                .map(binaryContent -> {
                    BinaryContent newBinaryContent = new BinaryContent(userUpdateRequestDTO.getBinaryContentCreateRequestDTO());
                    binaryContentRepository.save(newBinaryContent);
                    return newBinaryContent.getId();
                })
                .ifPresent(targetUser::updateProfileId);

        // 4. 사용자 변경 내용 저장
        userRepository.save(targetUser);

        // 5. 응답 DTO 변환 및 반환
        return toUserResponseDTO(targetUser, targetUserStatus);
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID userId) {
        // 1. 사용자 존재 여부 확인
        User targetUser = findUserEntityById(userId);

        // 2. 삭제된 사용자가 참여한 모든 채널 내 멤버에서 사용자 연쇄 삭제
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUserId().equals(userId))
                .forEach(channel -> {
                    channel.getMembers().removeIf(memberID -> memberID.equals(userId));
                    channelRepository.save(channel);
                });

        // 삭제된 사용자가 발행한 메시지 연쇄 삭제
        messageRepository.findAll().stream()
                .filter(message ->  message.getAuthorId().equals(userId))
                .forEach(messageRepository::delete);

        // 사용자 상태 연쇄 삭제 (추후 레파지토리에서 구현)
        userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(targetUser.getId()))
                .forEach(userStatus -> userStatusRepository.delete(userStatus.getId()));

        // 사용자 프로필 이미지 연쇄 삭제
        binaryContentRepository.findAll().stream()
                .filter(binaryContent -> binaryContent.getId().equals(targetUser.getProfileId()))
                .forEach(binaryContent -> binaryContentRepository.delete(binaryContent.getId()));

        // 3. 사용자 삭제
        userRepository.delete(targetUser);
    }

    // 유효성 검사 (이메일 중복)
    public void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 유효성 검사 (이름 중복)
    public void isNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname))
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
    }

    // 단일 엔티티 조회 및 반환
    public User findUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 엔티티 -> 응답 DTO 변환
    public UserResponseDTO toUserResponseDTO(User user, UserStatus userStatus) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profileId(user.getProfileId())
                .status(userStatus.getStatus())
                .build();
    }
}