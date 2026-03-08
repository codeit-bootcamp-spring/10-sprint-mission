package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    // 유저 생성 요청 DTO를 받아 유저 도메인 객체를 생성하고, 해당 객체 정보를 바탕으로 UserResponseDTO를 만들어 반환한다.
    @Override
    public UserDto create(UserCreateRequestDTO req, BinaryContentDto profileDto) {

        // 유저 레포지토리 내에서 유저 요청으로 들어온 이름과 이메일이 중복되는지 확인하고 중복 시 예외를 던진다.
        if (
            userRepository.existsByUsername(req.username())
                && userRepository.existsByEmail(req.email())) {
            throw new IllegalStateException("중복되는 유저 정보입니다.");
        }

        BinaryContent saved = null;

        // 유저 생성 요청 DTO에 binaryContent(첨부 파일)에 대한 정보가 들어가있다면...
        if (profileDto != null) {
            // 해당 첨부 파일 정보를 바탕으로 BinaryContent 객체를 생성과 동시에 BinaryRepository 영속화
            saved = binaryContentRepository.save(new BinaryContent(
                profileDto.fileName(),
                profileDto.size(),
                profileDto.contentType()
            ));

            // BinaryContentStorage 인터페이스 사용. UUID, MULTIPART의 Bytes를 Storage에 put.
            binaryContentStorage.put(saved.getId(), profileDto.bytes());
        }

        User user = new User(
            req.username(),
            req.email(),
            req.password(),
            saved
        );

        User savedUser = userRepository.save(user); // user 레포지토리의 save로 해당 유저 객체를 영속화한다.
        return userMapper.toDto(savedUser); // entities -> DTO
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

        return userMapper.toDto(user); // entities -> DTO
    }


    // 유저 레포지토리 내에 있는 모든 유저를 찾고 정보를 추출해서 UserResponseDTO를 반환하는 메소드
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(
                userMapper::toDto
            ).toList();

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
            // 해당 첨부 파일 정보를 바탕으로 BinaryContent 객체를 생성과 동시에 BinaryRepository 영속화
            saved = binaryContentRepository.save(new BinaryContent(
                profileDto.fileName(),
                profileDto.size(),
                profileDto.contentType()
            ));

            // BinaryContentStorage 인터페이스 사용. UUID, MULTIPART의 Bytes를 Storage에 put.
            binaryContentStorage.put(saved.getId(), profileDto.bytes());
        }

        // 유저 도메인 객체의 update 메소드를 통해 업데이트.
        user.update(req.newUsername(), req.newEmail(), req.newPassword(), saved);

        User savedUser = userRepository.save(user); // 영속화
        return userMapper.toDto(savedUser); // Entities -> DTO 후 리
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
