package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserSummaryResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;// 왜 불가능? -> 구현 클래스에 @Repository 필요한데 아직 구현 클래스 X
    private final UserStatusRepository userStatusRepository;// 왜 불가능?

    @Override
    public UserSummaryResponseDTO create(UserCreateRequestDTO userCreateRequestDTO,
                                         Optional<BinaryContentCreateRequestDTO> binaryContentCreateRequestDTO) {
        // DTO에서 @NotBlank와 같은 애너테이션을 이용해 검증함
        String username = userCreateRequestDTO.username();
        if (userRepository.findAll()
                .stream()
                .anyMatch(user -> username.equals(user.getUsername()))) {
            throw new IllegalStateException("이미 동일한 username을 갖고 있는 유저가 있습니다");
        }

        String email= userCreateRequestDTO.email();
        if (userRepository.findAll()
                .stream()
                .anyMatch(user -> email.equals(user.getEmail()))) {
            throw new IllegalStateException("이미 동일한 email을 갖고 있는 유저가 있습니다.");
        }

        String password = userCreateRequestDTO.password();

        User user;
        if (binaryContentCreateRequestDTO.isPresent()) { // 프로필 이미지 등록을 했다면
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentCreateRequestDTO.get().fileName(),
                    binaryContentCreateRequestDTO.get().contentType(),
                    binaryContentCreateRequestDTO.get().content()
            );
            binaryContentRepository.save(binaryContent);
            user = new User(username, email, password, binaryContent.getId());
        }else {// 프로필 이미지 등록을 안했다면
            user = new User(username,email,password,null);
        }
        UserStatus userStatus = new UserStatus(user.getId(),user.getCreatedAt());
        userStatusRepository.save(userStatus);
        userRepository.save(user);
        return toUserSummaryResponseDTO(user);
    }

    @Override
    public UserDetailResponseDTO find(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // UserStatusRepository에 userId를 통해 UserStatus를 찾는 메소드를 정의 해야함
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

        return toUserDetailResponseDTO(user,userStatus);
    }

    @Override
    public List<UserDetailResponseDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDetailResponseDTO> userDetailResponseDTOList = new ArrayList<>();

        for (User user : users) {
            UserStatus userStatus = getUserStatusByUserIdOrThrow(user.getId());
            UserDetailResponseDTO userDetailResponseDTO = toUserDetailResponseDTO(user, userStatus);
            userDetailResponseDTOList.add(userDetailResponseDTO);
        }
        return userDetailResponseDTOList;
    }

    @Override
    public UserSummaryResponseDTO update(UUID userId, UserUpdateRequestDTO userUpdateRequestDTO,
                                         Optional<BinaryContentCreateRequestDTO> binaryContentCreateRequestDTO) {
        // DTO에서 email을 애너테이션으로 검증
        // 수정하려는 newName같은 것들은 null을 허용 -> 원하는 유저의 필드를 선택적으로 수정핧 수 있게 하게끔
        User user = getUserByIdOrThrow(userId);
        String newUsername = userUpdateRequestDTO.newUsername();
        String newEmail = userUpdateRequestDTO.newEmail();
        String newPassword = userUpdateRequestDTO.newPassword();

        // 수정하려는 newUsername, newEmail이 기존의 다른 유저와 겹치면 안되기 때문에 검증 해야함
        boolean isDuplicated = userRepository.findAll()
                .stream()
                .anyMatch(u -> !u.getId().equals(userId) &&
                        (u.getUsername().equals(newUsername) || u.getEmail().equals(newEmail)));
        if (isDuplicated) {
            throw new IllegalStateException("수정하려는 새로운 username 또는 email를 사용중인 유저가 이미 있습니다");
        }

        if (binaryContentCreateRequestDTO.isPresent()) { // 프로필 이미지를 수정한다면
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentCreateRequestDTO.get().fileName(),
                    binaryContentCreateRequestDTO.get().contentType(),
                    binaryContentCreateRequestDTO.get().content()
            );
            binaryContentRepository.save(binaryContent);
            user.update(newUsername, newEmail, newPassword, binaryContent.getId());
        } else { // 프로필 이미지를 수정하지 않는다면
            // 기존 프로필 이미지 id를 사용
            user.update(newUsername, newEmail, newPassword, user.getProfileId());
        }
        userRepository.save(user);
        return toUserSummaryResponseDTO(user);
    }

    @Override
    // 관련 도메인도 삭제 - Binarycontent(프로필), UserStatus
    public void delete(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // UserStatus 삭제
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);
        userStatusRepository.deleteById(userStatus.getId());

        // BinaryContent(프로필) 삭제 (프로필 이미지가 없던 경우는 생략)
        if (!(user.getProfileId() == null)) {
            BinaryContent binaryContent = binaryContentRepository.findById(user.getProfileId())
                    .orElseThrow(() -> new NoSuchElementException(user.getProfileId()+"를 가진 BinaryContent를 찾지 못했습니다"));
            binaryContentRepository.deleteById(binaryContent.getId());
        }

        // User 삭제
        userRepository.deleteById(user.getId());
    }

    //find,findAll(온라인 정보를 포함)에서 반환할 DTO
    private UserDetailResponseDTO toUserDetailResponseDTO(User user, UserStatus userStatus) {
        return new UserDetailResponseDTO(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }

    //create, update에서 반환할 DTO
    private UserSummaryResponseDTO toUserSummaryResponseDTO(User user) {
        return new UserSummaryResponseDTO(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId()
        );
    }

    // userRepository.findById()를 통한 반복되는 user 조회/예외처리를 중복제거 하기 위한 메서드
    private User getUserByIdOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("userId:"+userId+"를 가진 user를 찾지 못했습니다"));
    }

    // userRepository.findByUserId()를 통한 반복되는 userStatus 조회/예외처리를 중복제거 하기 위한 메서드
    private UserStatus getUserStatusByUserIdOrThrow(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("userId:"+userId+"를 가진 userStatus를 찾지 못했습니다."));
    }
}
