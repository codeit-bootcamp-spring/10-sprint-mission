package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.DTO.UserCreateDTO;
import com.sprint.mission.discodeit.entity.DTO.UserFindAllDTO;
import com.sprint.mission.discodeit.entity.DTO.UserFindDTO;
import com.sprint.mission.discodeit.entity.DTO.UserUpdateDTO;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 생성자 주입
    public BasicUserService(UserRepository userRepository, UserStatusRepository userStatusRepository,BinaryContentRepository binaryContentRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public User create(UserCreateDTO userCreateDTO) {
        if (isExist(userCreateDTO.name(), userCreateDTO.email())) throw new NoSuchElementException();

        User user;
        if (userCreateDTO.profile() == null) {
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password());
        } else {
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password(), userCreateDTO.profile().getId());
        }

        UserStatus userStatus = new UserStatus(user.getId());
        this.userRepository.save(user);
        this.userStatusRepository.save(userStatus);
        return user;
    }

    @Override
    public UserFindDTO find(UUID id) {
        UserFindDTO userReadDTO;
        User user;
        UserStatus userStatus;

        for (User userCheck : this.userRepository.loadAll()) {
            if (userCheck.getId().equals(id)) {
                user = userCheck;
                userStatus = this.userStatusRepository.loadById(id);
                userReadDTO = new UserFindDTO(user, userStatus);
                return userReadDTO;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public UserFindAllDTO findAll() {
        UserFindAllDTO userFindAllDTO =
                new UserFindAllDTO(this.userRepository.loadAll(), this.userStatusRepository.loadAll());
        return userFindAllDTO;
    }

    @Override
    public User updateUser(UserUpdateDTO userUpdateDTO) {
        if (isExist(userUpdateDTO.userId())) throw new NoSuchElementException();
        User user = find(userUpdateDTO.userId()).user();
        if (userUpdateDTO.name().isEmpty()) {
            user.updateName(userUpdateDTO.name());
        }
        if (userUpdateDTO.profile() != null) {
            user.updateProfile(userUpdateDTO.profile().getId());
            this.binaryContentRepository.save(userUpdateDTO.profile());
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        UserFindDTO userFindDTO = this.find(id);
        if (userFindDTO.user().getProfileId() != null) {
            this.binaryContentRepository.delete(userFindDTO.user().getProfileId());
        }
        this.userStatusRepository.delete(id);
        this.userRepository.delete(id);
    }

    // 중복 검사
    public boolean isExist(String name, String email) {
        for (User user : this.userRepository.loadAll()) {
            String userName = user.getName();
            String userEmail = user.getEmail();
            if (userName.equals(name) || userEmail.equals(email))
                return true;
        }
        return false;
    }
    public boolean isExist(UUID userId) {
        for (User user : this.userRepository.loadAll()) {
            if (user.getId().equals(userId))
                return true;
        }
        return false;
    }

    // 특정 채널의 참가한 유저 목록 조회
    public List<User> readChannelUserList(UUID channelId, BasicChannelService channelService) {
        Channel channel = channelService.read(channelId);
        return channel.getUserList();
    }
}
