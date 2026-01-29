package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
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
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    //모든 멤버를 공개채널에 추가하기 위해..
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        validateEmail(dto.email());
        validateUsername(dto.username());
        //프로필 사진 없을때
        BinaryContent profile = null;
        if(dto.profileDto() != null) {
            profile=binaryContentRepository.save(new BinaryContent(dto.profileDto().fileName(), dto.profileDto().bytes()));
        }
        User user = new User(dto.username(),dto.email(),dto.password(),profile==null?null:profile.getId());
        UserStatus userStatus = new UserStatus(user.getId());
        //모든 공개채널에 대한 읽기 상태 저장
        channelRepository.findAllPublic()
                        .forEach(c->readStatusRepository.save(new ReadStatus(user.getId(),c.getId())));
        userRepository.save(user);
        userStatusRepository.save(userStatus);
        return UserMapper.userToDto(user,userStatus);
    }
    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        return UserMapper.userToDto(user,findUserStatusByUserId(userId));
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> response = new ArrayList<>();
        users.forEach(u-> response.add(UserMapper.userToDto(u,findUserStatusByUserId(u.getId()))));
        return response;
    }

    @Override
    public UserResponseDto update(UUID userId,UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        BinaryContent profile = null;
        if(dto.profileDto() != null) {
            profile =binaryContentRepository.save(new BinaryContent(dto.profileDto().fileName(), dto.profileDto().bytes()));
            binaryContentRepository.delete(user.getProfileId());//기존 프로필 사진 삭제
        }
        user.update(dto.username(), dto.email(), dto.password(),profile==null?null:profile.getId());
        return UserMapper.userToDto(userRepository.save(user),findUserStatusByUserId(userId));
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        if(user.getProfileId()!=null) {//프로필 사진 있는경우 삭제
            binaryContentRepository.delete(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        readStatusRepository.deleteByUserId(userId);//삭제된 사용자를 공개채널 멤버나 프라이빗 채널 멤버에서 제거
        userRepository.deleteById(userId);
    }
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일: "+email);
        }
    }
    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름: "+username);
        }
    }

    private UserStatus findUserStatusByUserId(UUID userId) {
      return userStatusRepository.findByUserId(userId)
           .orElseThrow(() -> new NoSuchElementException("UserStatus with Userid " + userId + " not found"));
    }

}
