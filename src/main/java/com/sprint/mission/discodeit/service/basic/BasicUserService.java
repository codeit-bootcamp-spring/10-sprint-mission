package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserDto.Response create(UserDto.Create request) {
        existsByUsername(request.username());
        existsByEmail(request.email());

        //프로필 설정하지 않으면 null
        UUID profileId = null;

        //요청에 프로필이 있다면 binaryContent 객체 생성 후 저장
        if (request.profile() != null && !request.profile().isEmpty()) {
            try {
                MultipartFile file = request.profile();
                BinaryContent profile = new BinaryContent(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        file.getBytes()
                );
                binaryContentRepository.save(profile);
                profileId = profile.getId();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );
        userRepository.save(user);

        //유저 상태 객체 생성 후 저장
        UserStatus status = new UserStatus(user.getId());
        status.updateOnline();
        userStatusRepository.save(status);

        return UserDto.Response.of(user, status);
    }

    @Override
    public UserDto.Response findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 상태입니다."));
        return UserDto.Response.of(user, status);
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 상태입니다."));
                    return UserDto.Response.of(user, status);
                })
                .toList();
    }

    @Override
    public UserDto.Response update(UUID userId, UserDto.Update request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        Optional.ofNullable(request.username()).ifPresent(user::updateUsername);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        Optional.ofNullable(request.password()).ifPresent(user::updatePassword);

        if (request.profile() != null && !request.profile().isEmpty()) { //요청에 프로필 파일이 있는지 확인
            if (user.getProfileId() != null) { //기존 유저에게 프로필이 있는지 확인, 프로필이 있으면 지움
                binaryContentRepository.findById(user.getProfileId())
                        .ifPresent(binaryContentRepository::delete);
            }

            try {
                MultipartFile file = request.profile();
                BinaryContent newProfile = new BinaryContent(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        file.getBytes()
                );
                binaryContentRepository.save(newProfile);
                user.updateProfileId(newProfile.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //프로필 말고 다른 변경사항이 있을 수 있으니 if문 밖에서 저장
        userRepository.save(user);

        //유저 상태 객체 획인 후 온라인으로 갱신
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 상태입니다."));
        status.updateOnline();

        userStatusRepository.save(status);
        return UserDto.Response.of(user, status);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        //유저가 작성한 메시지 목록
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
        //메시지 속 첨부파일 삭제, 메시지 삭제
        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .ifPresent(binaryContentRepository::delete);
            }
            messageRepository.delete(message);
        }

        //읽음 상태 삭제
        readStatusRepository.findAllByUserId(userId)
                .forEach(readStatusRepository::delete);

        //유저 상태 삭제
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 상태입니다."));
        userStatusRepository.delete(status);

        //유저 프로필 삭제
        Optional.ofNullable(user.getProfileId())
                .flatMap(binaryContentRepository::findById)
                .ifPresent(binaryContentRepository::delete);

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
