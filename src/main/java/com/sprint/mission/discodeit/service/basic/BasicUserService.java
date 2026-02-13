package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    //모든 멤버를 공개채널에 추가하기 위해..
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserDto create(UserCreateDto dto, Optional<BinaryContentCreateDto> binaryContentCreateDto) {
        validateEmail(dto.email());
        validateUsername(dto.username());
        //프로필 사진
        BinaryContent profile = null;
        if(binaryContentCreateDto.isPresent()) {
            profile = binaryContentRepository.save(binaryContentMapper.toEntity(binaryContentCreateDto.get()));
        }
        User user =  userMapper.toEntity(dto, profile);
        UserStatus userStatus = new UserStatus(user.getId());
        //모든 공개채널에 대한 읽기 상태 저장
        channelRepository.findAllPublic()
                        .forEach(c->readStatusRepository.save(new ReadStatus(user.getId(),c.getId())));
        userRepository.save(user);
        userStatusRepository.save(userStatus);
        return userMapper.toDto(user,userStatus);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = get(userId);
        return userMapper.toDto(user,findUserStatusByUserId(userId));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> response = new ArrayList<>();
        users.forEach(u-> response.add(userMapper.toDto(u,findUserStatusByUserId(u.getId()))));
        return response;
    }

    @Override
    public UserDto update(UUID userId, UserUpdateDto dto, Optional<BinaryContentCreateDto> binaryContentCreateDto) {
        User user = get(userId);
        BinaryContent profile = null;
        UUID oldProfileId = null;
        if(binaryContentCreateDto.isPresent()) {
            profile = binaryContentRepository.save(binaryContentMapper.toEntity(binaryContentCreateDto.get()));
            oldProfileId = user.getProfileId();
        }
        user.update(dto.username(), dto.email(), dto.password(),profile==null?null:profile.getId());
        if(oldProfileId!=null) {//기존 프로필을 가지고 있는경우만
            binaryContentRepository.delete(oldProfileId);//기존 프로필 사진 삭제
        }
        return userMapper.toDto(userRepository.save(user),findUserStatusByUserId(userId));
    }

    @Override
    public void delete(UUID userId) {
        User user = get(userId);
        if(user.getProfileId()!=null) {//프로필 사진 있는경우 삭제
            binaryContentRepository.delete(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        readStatusRepository.deleteByUserId(userId);//삭제된 사용자를 공개채널 멤버나 프라이빗 채널 멤버에서 제거
        userRepository.deleteById(userId);
    }
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXIST);
        }
    }
    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessLogicException(ExceptionCode.USER_NAME_ALREADY_EXIST);
        }
    }

    private UserStatus findUserStatusByUserId(UUID userId) {
      return userStatusRepository.findByUserId(userId)
           .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    }

    private User get(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }
}
