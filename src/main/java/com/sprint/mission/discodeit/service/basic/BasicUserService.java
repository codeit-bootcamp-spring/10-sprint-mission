package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;// 왜 불가능? -> 구현 클래스에 @Repository 필요한데 아직 구현 클래스 X
    private final UserStatusRepository userStatusRepository;// 왜 불가능?
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
                                         Optional<BinaryContentCreateRequest> binaryContentCreateRequestDTO) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 동일한 username을 갖고 있는 유저가 있습니다");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 동일한 email을 갖고 있는 유저가 있습니다");
        }

        String password = userCreateRequest.password();

        User user;
        if (binaryContentCreateRequestDTO.isPresent()) { // 프로필 이미지 등록을 했다면
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentCreateRequestDTO.get().fileName(),
                    (long)binaryContentCreateRequestDTO.get().bytes().length,
                    binaryContentCreateRequestDTO.get().bytes(),
                    binaryContentCreateRequestDTO.get().contentType()
            );
            user = new User(username, email, password, binaryContent);// 여기서 binaryContent와 연결
        }else {// 프로필 이미지 등록을 안했다면
            user = new User(username,email,password,null);
        }
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userStatus.setUser(user);// 연관관계 매핑(User와 UserStatus 서로 연결됨)
        userRepository.save(user);// user가 binaryContent, userStatus 들고있으니 한 번에 저장
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // UserStatusRepository에 userId를 통해 UserStatus를 찾는 메소드를 정의 해야함
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users) {
            UserStatus userStatus = getUserStatusByUserIdOrThrow(user.getId());
            UserDto userDto = userMapper.toDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                                         Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        // 수정하려는 newName같은 것들은 null을 허용 -> 원하는 유저의 필드를 선택적으로 수정핧 수 있게 하게끔
        User user = getUserByIdOrThrow(userId);
        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        // 수정하려는 newUsername, newEmail이 기존의 다른 유저와 겹치면 안되기 때문에 검증 해야함
        boolean isDuplicated = userRepository.existsByUsernameOrEmailAndIdNot(newUsername, newEmail, user.getId());
        if (isDuplicated) {
            throw new IllegalArgumentException("수정하려는 새로운 username 또는 email를 사용중인 유저가 이미 있습니다");
        }
        if (binaryContentCreateRequest.isPresent()) { // 프로필 이미지를 수정한다면
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentCreateRequest.get().fileName(),
                    (long)binaryContentCreateRequest.get().bytes().length,
                    binaryContentCreateRequest.get().bytes(),
                    binaryContentCreateRequest.get().contentType()
            );
            user.update(newUsername, newEmail, newPassword, binaryContent);
        } else { // 프로필 이미지를 수정하지 않는다면
            // 기존 프로필 이미지를 사용
            user.update(newUsername, newEmail, newPassword, user.getProfile());
        }
        userRepository.save(user);// 이때 binaryContent도 같이 저장
        return userMapper.toDto(user);
    }

    @Override
    // 관련 도메인도 삭제 - Binarycontent(프로필), UserStatus
    public void delete(UUID userId) {
        User user = getUserByIdOrThrow(userId);
        // User 삭제(UserStatus, BinaryContent(프로필) 둘다 삭제됨)
        userRepository.deleteById(user.getId());
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
