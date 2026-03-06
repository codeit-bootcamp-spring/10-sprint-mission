package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserDTOMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository; // 아직 인터페이스 구현체가 없어서 bean을 못찾음.
    private final UserStatusRepository userStatusRepository; // 이하 동문

    // 유저 생성 요청 DTO를 받아 유저 도메인 객체를 생성하고, 해당 객체 정보를 바탕으로 UserResponseDTO를 만들어 반환한다.
    @Override
    public UserDto create(UserCreateRequestDTO req, BinaryContentDto profileDto) {

        // 유저 레포지토리 내에서 유저 요청으로 들어온 이름과 이메일이 중복되는지 확인하고 중복 시 예외를 던진다.
        if (
            userRepository.existsByUsername(req.username())
                && userRepository.existsByEmail(req.email())) {
            throw new IllegalStateException("중복되는 유저 정보입니다.");
        }
        // 유저 도메인 객체 생성 시 들어갈 profileID는 null로 초기화한다.
//        UUID profileId = null;
        BinaryContent profile = null;

        // 유저 생성 요청 DTO에 binaryContent(첨부 파일)에 대한 정보가 들어가있다면...
        if (profileDto != null) {
            // 해당 첨부 파일 정보를 바탕으로 BinaryContent 객체를 생성한다.
            BinaryContent binaryContent = new BinaryContent(
                profileDto.fileName(),
                profileDto.size(),
                profileDto.contentType(),
                profileDto.bytes()
            );

            // binaryContent 레포지토리를 통해 해당 객체를 영속화한다.
            //            profileId = saved.getId(); // null로 초기화했던 profileID를 해당 객체의 ID로 초기화한다.
            profile = binaryContentRepository.save(binaryContent);
        }

        User user = UserDTOMapper.regtoUser(
            req,
            profile
        ); // DTO -> Entity를 통해 생성 요청 DTO에서 유저 객체를 생성한다.

        User savedUser = userRepository.save(user); // user 레포지토리의 save로 해당 유저 객체를 영속화한다.
        UserStatus savedUserStatus = userStatusRepository.save(
            new UserStatus(savedUser)); // UserStatus 객체를 새로 만들고 레포의 save를 통해 영속화

        return UserDTOMapper.userToResponse(
            savedUser,
            savedUserStatus.isOnline()); // entities -> DTO
    }

    // 유저 ID로 해당 유저가 레포내에 존재하는지 찾고 UserResponseDTO를 반환하는 메소드
    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        // 유저 레포에 해당 유저가 존재하는지 확인하고 미존재시 예외 던짐.
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new IllegalStateException("유저 ID가 존재하지 않습니다.")
            );

        // 유저 상태 레포에서 find
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("해당 User Status 객체는 존재하지 않습니다!"));

        return UserDTOMapper.userToResponse(user, userStatus.isOnline()); // entities -> DTO
    }


    // 유저 레포지토리 내에 있는 모든 유저를 찾고 정보를 추출해서 UserResponseDTO를 반환하는 메소드
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(
                u -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(u.getId())
                        .orElseThrow(() -> new NoSuchElementException("UserStatus 찾을 수 없음!"));
                    return UserDTOMapper.userToResponse(u, userStatus.isOnline());
                }).toList();

    }

    // 유저 업데이트 DTO를 받아 해당 객체를 업데이트하고 UserResponseDTO를 반환하는 메소드
    @Override
    public UserDto update(UUID userId, UserUpdateDTO req, BinaryContentDto profileDto) {
        // 유저 레포지토리에서 요청에 담긴 userID를 통해 유저를 찾고 없으면 예외를 던짐
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("User with id " + userId + " not found")
            );

        BinaryContent saved = null;
        // profileDto(BinaryContentDto)가 존재한다면...
        if (profileDto != null) {
            // 해당 BinaryContentDTO를 통해 binaryContent를 생성
            BinaryContent binaryContent = new BinaryContent(
                profileDto.fileName(),
                profileDto.size(),
                profileDto.contentType(),
                profileDto.bytes()
            );

            saved = binaryContentRepository.save(binaryContent); // 해당 BinaryContent를 영속화함.
        }

        // 유저 도메인 객체의 update 메소드를 통해 업데이트.
        user.update(req.newUsername(), req.newEmail(), req.newPassword(), saved);

        User savedUser = userRepository.save(user); // 영속화
        UserStatus userStatus = userStatusRepository.findByUserId(savedUser.getId())
            .orElseThrow(() -> new NoSuchElementException("UserStatus 찾을 수 없음!"));
        return UserDTOMapper.userToResponse(savedUser,
            userStatus.isOnline()); // Entities -> DTO 후 리
    }

    // 지우고자 하는 유저를 지우면서 관련된 객체(UserStatus)와 정보(채널 가입 여부 및 메시지)도 같이 삭제하는 메소드
    @Override
    @Transactional
    public void delete(UUID userId) {
        // 해당 유저 ID가 레포지토리 내에 존재하는지 확인하고 없으면 예외 던짐.
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않음"));
        userRepository.deleteById(userId);
    }
}
