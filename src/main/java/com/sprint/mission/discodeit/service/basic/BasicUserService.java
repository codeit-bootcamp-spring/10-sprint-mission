package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    // 사용할 저장소
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {

        // username 중복 검사
        if (userRepository.existsByName(request.userName())) {
            throw new DuplicationUserException();
        }

        //email 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicationEmailException();
        }

        //User 엔티티 생성 (DTO -> Entity 변환)
        User user = new User(
                request.userName(),
                request.email(),
                request.password()  // 나중에 AuthService
        );

        UserStatus status = new UserStatus(user.getId(), Instant.now());

        //프로필 이미지저장 (있다면)
        if (request.profileImage() != null) {
            ProfileImageCreateRequest imgReq = request.profileImage();

            BinaryContent image = new BinaryContent(
                    imgReq.fileName(),
                    imgReq.contentType(),
                    imgReq.data(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(image);
            user.updateProfileImage(image.getId());
        }

        userRepository.save(user);
        userStatusRepository.save(status);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                status.isOnline(),
                status.getLastSeenAt(),
                user.getProfileImageId()
        );
    }

    @Override
    public UserResponse find(UUID userId) {

        //유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        //유저 상태 조회
        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) throw new StatusNotFoundException();


        return new  UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                status.isOnline(),
                status.getLastSeenAt(),
                user.getProfileImageId()
        );
    }

    @Override
    public List<UserResponse> findAll() {

        //모든 유저 조회
        List<User> users = userRepository.findAll();

        //User → UserResponse 변환
        return users.stream()
                .map(user -> {

                    // 각 유저의 상태 조회
                    UserStatus status = userStatusRepository.findByUserId(user.getId());
                    if (status == null) throw new StatusNotFoundException();


                    // DTO 변환 (비밀번호 제외)
                    return new UserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            status.isOnline(),
                            status.getLastSeenAt(),
                            user.getProfileImageId()
                    );
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {

        //수정 대상 유저 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        //username 변경
        request.userName().ifPresent(newName -> {
            if (!user.getName().equals(newName) &&
                    userRepository.existsByName(newName)) {
                throw new DuplicationUserException();
            }
            user.updateName(newName); // 엔티티 메서드 필요
        });

        //email 변경
        request.email().ifPresent(newEmail -> {
            if (!user.getEmail().equals(newEmail) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new DuplicationEmailException();
            }
            user.updateEmail(newEmail); // 엔티티 메서드 필요
        });

        //password 변경
        request.password().ifPresent(newPassword -> {
            if(newPassword.isEmpty()){
                throw new ZeroLengthPasswordException();
            }

            user.updatePassword(newPassword);
        });


        //프로필 이미지 교체
        request.profileImage().ifPresent(imgReq -> {
            // 기존 이미지 삭제
            if (user.getProfileImageId() != null) {
                binaryContentRepository.delete(user.getProfileImageId());
            }

            // 새 이미지 저장
            BinaryContent newImage = new BinaryContent(
                    imgReq.fileName(),
                    imgReq.contentType(),
                    imgReq.data(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(newImage);
            user.updateProfileImage(newImage.getId());

        });

        userRepository.save(user);

        //상태 조회
        UserStatus status = userStatusRepository.findByUserId(user.getId());
        if (status == null) throw new StatusNotFoundException();


        //DTO 반환
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                status.isOnline(),
                status.getLastSeenAt(),
                user.getProfileImageId()
        );
    }

    @Override
    public void delete(UUID userId) {

        //유저 존재 확인 (없으면 예외)
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        //프로필 이미지가 있으면 파일도 삭제
        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }
        userStatusRepository.deleteByUserId(userId);
        readStatusRepository.deleteByUserId(userId);
        userRepository.delete(userId);
    }
}