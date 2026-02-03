package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserId;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserStatusService {
    private UserStatusRepository userStatusRepository;
    private UserRepository userRepository;

    public UserStatusService(UserStatusRepository userStatusRepository, UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    public UserStatusDTO create(UserStatusCreateDTO userStatusCreateDTO) {
        // 관련된 User가 존재하지 않으면 예외 발생
        User user = this.userRepository.loadById(userStatusCreateDTO.userId());

        for (UserStatus userStatusCheck : this.userStatusRepository.loadAll()) {
            if (userStatusCheck.getUserId().equals(userStatusCreateDTO.userId())) {
                throw new NoSuchElementException();
            }
        }

        UserStatus userStatus = new UserStatus(userStatusCreateDTO.userId());
        UserStatusDTO userStatusDTO = new UserStatusDTO(userStatus.getId(), userStatus);

        return userStatusDTO;
    }

    public UserStatusDTO findById(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository.loadById(userStatusId);
        return new UserStatusDTO(userStatus.getId(), userStatus);
    }

    public List<UserStatusDTO> findAll() {
        List<UserStatusDTO> userStatusDTOList = new ArrayList<>();

        for (UserStatus userStatus : this.userStatusRepository.loadAll()) {
            UserStatusDTO userStatusDTO = new UserStatusDTO(userStatus.getId(), userStatus);
            userStatusDTOList.add(userStatusDTO);
        }

        return userStatusDTOList;
    }

    public UserStatusDTO update(UserStatusUpdateDTO userStatusUpdateDTO) {
        UserStatus userStatus = this.findById(userStatusUpdateDTO.userStatusId()).userStatus();
        userStatus.updateOnline(userStatusUpdateDTO.isOnline());
        return new UserStatusDTO(userStatus.getId(), userStatus);
    }

    public UserStatusDTO updateByUserId(UserStatusUpdateByUserId userStatusUpdateByUserId) {
        for (UserStatus userStatus : this.userStatusRepository.loadAll()) {
            if (userStatus.getUserId().equals(userStatusUpdateByUserId.userId())) {
                userStatus.updateOnline(userStatusUpdateByUserId.isOnline());
                return new UserStatusDTO(userStatus.getId(), userStatus);
            }
        }
        throw new NoSuchElementException();
    }

    public void delete(UUID userStatusId) {
        this.userStatusRepository.delete(userStatusId);
    }
}
