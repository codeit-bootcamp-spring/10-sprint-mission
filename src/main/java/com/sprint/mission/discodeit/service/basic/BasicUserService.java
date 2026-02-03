package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.entity.*;
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
    public UserDTO create(UserCreateDTO userCreateDTO) {
        if (isExistName(userCreateDTO.name()) || isExistEmail(userCreateDTO.email())) throw new NoSuchElementException();

        User user;
        if (userCreateDTO.profileImage() == null) {
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password());
        } else {
            BinaryContent profile = new BinaryContent(userCreateDTO.profileImage().fileName(), userCreateDTO.profileImage().fileType(), userCreateDTO.profileImage().bytes());
            user = new User(userCreateDTO.name(), userCreateDTO.email(), userCreateDTO.password(), profile.getId());
            this.binaryContentRepository.save(profile);
        }

        UserStatus userStatus = new UserStatus(user.getId());
        this.userRepository.save(user);
        this.userStatusRepository.save(userStatus);
        return createUserDTO(user, userStatus);
    }

    @Override
    public UserDTO findById(UUID id) {
        User user = this.userRepository.loadById(id);
        UserStatus userStatus = this.userStatusRepository.loadById(id);
        return createUserDTO(user, userStatus);

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
    public List<UserDTO> findAll() {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : this.userRepository.loadAll()) {
            userDTOList.add(createUserDTO(user, this.userStatusRepository.loadById(user.getId())));
        }
        return userDTOList;
    }

    @Override
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO) {
        if (isExistId(userUpdateDTO.userId())) throw new NoSuchElementException();
        User user = this.userRepository.loadById(userUpdateDTO.userId());
        if (!userUpdateDTO.name().isEmpty()) {
            user.updateName(userUpdateDTO.name());
        }
        if (userUpdateDTO.profile() != null) {
            user.updateProfile(userUpdateDTO.profile().getId());
            this.binaryContentRepository.save(userUpdateDTO.profile());
        }

        return createUserDTO(user, this.userStatusRepository.loadById(user.getId()));
    }

    @Override
    public void delete(UUID userId) {
        User user = this.userRepository.loadById(userId);
        if (user.getProfileId() != null) {
            this.binaryContentRepository.delete(user.getProfileId());
        }
        this.userStatusRepository.delete(userId);
        this.userRepository.delete(userId);
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

    // DTO생성
    public UserDTO createUserDTO(User user, UserStatus userStatus) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                user.getChannelList(),
                user.getMessageList(),
                new UserStatusDTO(userStatus.getId(), userStatus.checkOnline()));
    }

//    // 특정 채널의 참가한 유저 목록 조회
//    public List<User> readChannelUserList(UUID channelId, BasicChannelService channelService) {
//        Channel channel = channelService.findById(channelId);
//        return channel.getUserList();
//    }
}
