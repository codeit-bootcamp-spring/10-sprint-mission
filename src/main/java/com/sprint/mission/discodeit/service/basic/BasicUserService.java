package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ProfileCreateRequestDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;// 왜 불가능? -> 구현 클래스에 @Repository 필요한데 아직 구현 클래스 X
    private final UserStatusRepository userStatusRepository;// 왜 불가능?

//    @RequiredArgsConstructor로 대체
//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

//    @Override
//    public User create(String username, String email, String password) {
//        User user = new User(username, email, password);
//        return userRepository.save(user);
//    }
    @Override
    public UserSummaryResponseDTO create(UserCreateRequestDTO userCreateRequestDTO) {
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
        ProfileCreateRequestDTO profileImage = userCreateRequestDTO.profileImage();
        if (profileImage == null) {// 프로필 이미지 등록을 안했다면
            user = new User(username,email,password, null);
        } else { // 프로필 등록을 했다면
            byte[] content = profileImage.content();
            String contentType = profileImage.contentType();
            BinaryContent binaryContent = new BinaryContent(contentType,content);
            binaryContentRepository.save(binaryContent);
            user = new User(username, email, password, binaryContent.getId());
        }
        UserStatus userStatus = new UserStatus(user.getId(),user.getCreatedAt());
        userStatusRepository.save(userStatus);
        userRepository.save(user);
        return toUserSummaryResponseDTO(user);
    }

//    @Override
//    public User find(UUID userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
//    }


    @Override
    public UserDetailResponseDTO find(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // UserStatusRepository에 userId를 통해 UserStatus를 찾는 메소드를 정의 해야함
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

        return toUserDetailResponseDTO(user,userStatus);
    }

//    @Override
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }


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
    public UserSummaryResponseDTO update(UUID userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        // DTO에서 email을 애너테이션으로 검증
        // 수정하려는 newName같은 것들은 null을 허용 -> 원하는 유저의 필드를 선택적으로 수정핧 수 있게 하게끔
        User user = getUserByIdOrThrow(userId);
        String newUsername = userUpdateRequestDTO.newUsername();
        String newEmail = userUpdateRequestDTO.newEmail();
        String newPassword = userUpdateRequestDTO.newPassword();

        // 수정하려는 newUsername, newEmail이 기존의 다른 유저와 겹치면 안되기 때문에 검증 해야함
        // username, email이 같은지 확인하기 위해 findAll()?
        // 아니면 repository에 findByUsername, findByEmail 같은 메서드를 정의 후 여기서 사용?
        // repository에 구체적인 메서드를 정의 후 service 에서 사용???
        boolean canUpdate = userRepository.findAll()
                .stream()
                .anyMatch(u -> !u.getUsername().equals(newUsername) &&
                        !u.getEmail().equals(newEmail) &&
                        !u.getId().equals(userId));
        if (!canUpdate) {
            throw new IllegalStateException("수정하려는 새로운 username 또는 email를 사용중인 유저가 이미 있습니다");
        }

        // 프로필 이미지를 선택적으로 수정할 수 있어야함 -> null 확인
        ProfileCreateRequestDTO profileImage = userUpdateRequestDTO.profileImage();
        if (profileImage == null) {// 프로필 수정 안했다면
            // User class update() 입력 파라미터에 profileId를 추가해야함
            // user.getProfileId()를 사용해 기존 프로필 이미지의 id를 입력값으로
            user.update(newUsername, newEmail, newPassword, user.getProfileId());
        }else {// 프로필 이미지를 수정한다면
            String contentType = profileImage.contentType();
            byte[] content = profileImage.content();
            BinaryContent binaryContent = new BinaryContent(contentType,content);
            binaryContentRepository.save(binaryContent);
            user.update(newUsername, newEmail, newPassword, binaryContent.getId());
        }
        userRepository.save(user);
        return toUserSummaryResponseDTO(user);
    }

    @Override
    // 관련 도메인도 삭제 - Binarycontent(프로필), UserStatus
    public void delete(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // UserStatus 삭제
        // UserStatus를 userId로 찾고 .getId()를 이용해서 deleteById를 해야하는지?
        // 아니면 UserStatusRepository에 deleteByUserId()를 정의하고 service에서 사용해야하는지?
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
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline(),
                user.getProfileId()
        );
    }

    //create, update에서 반환할 DTO
    private UserSummaryResponseDTO toUserSummaryResponseDTO(User user) {
        return new UserSummaryResponseDTO(
                user.getId(),
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
