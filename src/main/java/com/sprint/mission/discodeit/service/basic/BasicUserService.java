package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserCreateResponseDTO;
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
    public UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO, BinaryContentCreateRequestDTO binaryContentCreateRequestDTO) {
        // 1. 유효성 검증 (이메일 중복, 이름 중복)
        isEmailDuplicate(userCreateRequestDTO.getEmail());
        isNicknameDuplicate(userCreateRequestDTO.getNickname());

        // 2. User 생성, UserStatus 같이 생성
        User newUser = new User(userCreateRequestDTO);
        userRepository.save(newUser);

        UserStatus newUserStatus = new UserStatus(newUser.getId());
        userStatusRepository.save(newUserStatus);

        // 3. 선택적 프로필 이미지 생성 및 저장
        UUID profileImageId = Optional.ofNullable(binaryContentCreateRequestDTO.getBinaryContent()).map(content -> {
            BinaryContent newBinaryContent = new BinaryContent(binaryContentCreateRequestDTO);
            binaryContentRepository.save(newBinaryContent);
            return newBinaryContent.getId();
        });

        // 5. 응답 DTO 객체 생성 및 반환
        return UserCreateResponseDTO.builder()
                .id(newUser.getId())
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .profileId(profileImageId)
                .status(newUserStatus.getStatus())
                .build();
    }

    // 사용자 단건 조회
    @Override
    public User searchUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    // 사용자 전체 조회
    @Override
    public List<UserCreateResponseDTO> searchUserAll() {
        return userRepository.findAll();
    }

    // 특정 채널의 참가자 목록 조회
    @Override
    public List<UUID> searchMembersByChannelId(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."))
                .getMembers();
    }

    // 사용자 정보 수정
    @Override
    public User updateUser(UUID userId, String newPassword, String newNickname, UserStatusType newUserStatus) {
        User targetUser = searchUser(userId);

        Optional.ofNullable(newPassword)        // 비밀번호 필드 변경
                .ifPresent(password -> {
                    validateString(password, "[비밀 번호 변경 실패] 올바른 비밀 번호 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getPassword(), newPassword, "[비밀 번호 변경 실패] 현재 비밀 번호와 일치합니다.");
                    targetUser.updatePassword(password);
                });

        Optional.ofNullable(newNickname)        // 닉네임 필드 변경
                .ifPresent(nickname -> {
                    validateString(nickname, "[닉네임 변경 실패] 올바른 닉네임 형식이 아닙니다.");
                    validateDuplicateValue(targetUser.getNickname(), newNickname, "[닉네임 변경 실패] 현재 닉네임과 일치합니다.");
                    targetUser.updateNickname(nickname);
                });

        userRepository.save(targetUser);
        return targetUser;
    }

    // 사용자 삭제
    @Override
    public void deleteUser(UUID userId) {
        User targetUser = searchUser(userId);

        channelRepository.findAll().stream()
                .filter(channel -> channel.getUserId().equals(userId))
                .forEach(channel -> {
                    channel.getMembers().removeIf(memberID -> memberID.equals(userId));
                    channelRepository.save(channel);
                });

        messageRepository.findAll().stream()          // 모든 메시지에서 해당 유저가 작성한 메시지 제거
                .filter(message -> message != null /*&& targetUser != null*/)
                .filter(message -> message.getAuthorId().equals(userId))
                .forEach(messageRepository::delete);

        userRepository.delete(targetUser);
    }

    @Override
    public void updateUser(UUID userId, User user) {
        userRepository.save(user);
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
}