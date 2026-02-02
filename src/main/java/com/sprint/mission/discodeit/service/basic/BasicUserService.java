package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserFindAllDTO;
import com.sprint.mission.discodeit.dto.user.UserFindDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;
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
        if (isExistName(userCreateDTO.name()) || isExistEmail(userCreateDTO.email())) throw new NoSuchElementException();

        User user;
        if (userCreateDTO.profileImage() == null) {
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password());
        } else {
            BinaryContent profile = new BinaryContent(userCreateDTO.profileImage().fileName(), userCreateDTO.profileImage().fileType());
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password(), profile.getId());
            this.binaryContentRepository.save(profile);
        }

        UserStatus userStatus = new UserStatus(user.getId());
        this.userRepository.save(user);
        this.userStatusRepository.save(userStatus);
        return user;
    }

    @Override
    public UserFindDTO find(UUID id) {
        UserFindDTO userFindDTO;
        User user = this.userRepository.loadById(id);
        UserStatus userStatus = this.userStatusRepository.loadById(id);
        userFindDTO = new UserFindDTO(user, userStatus);
        return userFindDTO;

//        for (User userCheck : this.userRepository.loadAll()) {
//            if (userCheck.getId().equals(id)) {
//                user = userCheck;
//                userStatus = this.userStatusRepository.loadById(id);
//                userFindDTO = new UserFindDTO(user, userStatus);
//                return userFindDTO;
//            }
//        }
//        throw new NoSuchElementException();
    }

    @Override
    public UserFindAllDTO findAll() {
        UserFindAllDTO userFindAllDTO =
                new UserFindAllDTO(this.userRepository.loadAll(), this.userStatusRepository.loadAll());
        return userFindAllDTO;
    }

    @Override
    public User updateUser(UserUpdateDTO userUpdateDTO) {
        if (isExistId(userUpdateDTO.userId())) throw new NoSuchElementException();
        User user = find(userUpdateDTO.userId()).user();
        if (!userUpdateDTO.name().isEmpty()) {
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
    public boolean isExistName(String name) {
        for (User user : this.userRepository.loadAll()) {
            String userName = user.getName();
            if (userName.equals(name))
                return true;
        }
        return false;
    }
    public boolean isExistEmail(String email) {
        for (User user : this.userRepository.loadAll()) {
            String userEmail = user.getEmail();
            if (userEmail.equals(email))
                return true;
        }
        return false;
    }
    public boolean isExistId(UUID userId) {
        for (User user : this.userRepository.loadAll()) {
            if (user.getId().equals(userId))
                return true;
        }
        return false;
    }

//    // 특정 채널의 참가한 유저 목록 조회
//    public List<User> readChannelUserList(UUID channelId, BasicChannelService channelService) {
//        Channel channel = channelService.findById(channelId);
//        return channel.getUserList();
//    }
}
