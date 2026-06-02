package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailFoundException(request.getEmail());
        }

        // 유저 객체 생성
        User user = new User(request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER);

        // 프로필 등록 여부 & binaryContent객체 생성
        if(profile != null){
            try{
                BinaryContent binaryContent = new BinaryContent(
                    profile.getSize(),
                    profile.getOriginalFilename(),
                    profile.getContentType());

                user.addProfileImage(binaryContent);
                // 연관성 주입
                binaryContent = binaryContentRepository.save(binaryContent);
                applicationEventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), profile.getBytes()));
                log.info("파일 업로드 성공: 유저 email = {}, 파일 id = {}, 파일 이름 = {}",
                        request.getEmail(), binaryContent.getId(), binaryContent.getFileName());

            } catch (Exception e) {
                throw new FileUploadFailException();
            }
        }
        // 유저 저장
        userRepository.save(user);

        return userMapper.toDto(user,true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUser(UUID userId) {
        User user = getUser(userId);

        UserDto dummyDto = userMapper.toDto(user,true);
        boolean online =  authService.isOnline(dummyDto);
        log.trace("유저 조회 성공: 사용자 id = {}, 이름 = {}, 온라인 상태 = {}",user.getId(), user.getUsername(), online);
        return userMapper.toDto(user,online);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<User> userList = userRepository.findAll();
        if(!userList.isEmpty()){
            // 가져온 객체들을 dto로 변환
            return userList.stream()
                    .map(user -> {
                        UserDto dummyDto = userMapper.toDto(user,true);
                        boolean online =  authService.isOnline(dummyDto);

                        return userMapper.toDto(user, online);
                    })
                    .toList();
        } else{
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    @PreAuthorize("#userId == principal.userDto.id")
    public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile) {
        User user = getUser(userId);
        // 이름 수정
        if(request.getNewUsername() != null){
            user.updateName(request.getNewUsername());
        }
        // 이메일 수정
        if(request.getNewEmail() != null){
            if(userRepository.existsByEmail(request.getNewEmail())){
                throw new DuplicateEmailFoundException(request.getNewEmail());
            }
            user.updateEmail(request.getNewEmail());
        }
        // 비밀번호 수정
        if(request.getNewPassword() != null){
            String newPassword = passwordEncoder.encode(request.getNewPassword());
            user.updatePassword(newPassword);
        }
        // 프로필 수정(기존에 있던 binaryContent를 삭제하고 업데이트 dto에 있는 binaryContent를 생성
        if(profile != null){
            if(user.getProfile() != null) binaryContentRepository.delete(user.getProfile());
            try{
                BinaryContent newBinaryContent = new BinaryContent(
                        profile.getSize(),
                        profile.getOriginalFilename(),
                        profile.getContentType());
                newBinaryContent = binaryContentRepository.save(newBinaryContent);
                applicationEventPublisher.publishEvent(new BinaryContentCreatedEvent(newBinaryContent.getId(), profile.getBytes()));

                user.updateProfileImg(newBinaryContent);
            } catch (Exception e) {
                throw new FileUploadFailException();
            }
        }
        log.info("유저 정보 수정 성공: 유저 id = {}", userId);
        return findUser(userId);
    }

    @Override
    @Transactional
    @PreAuthorize("#userId == principal.userDto.id")
    public void delete(UUID userId) {
        User user = getUser(userId);
        BinaryContent profileImg = user.getProfile();

        // 유저를 데이터에서 삭제
        userRepository.delete(user);

        // 유저가 들고 있던 바이너리 컨텐츠 삭제
        if( profileImg != null){
            binaryContentRepository.delete(profileImg);
        }
        log.info("유저 삭제 성공: 유저 id = {}", userId);
    }

    // 유효성 검사
    private User getUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
