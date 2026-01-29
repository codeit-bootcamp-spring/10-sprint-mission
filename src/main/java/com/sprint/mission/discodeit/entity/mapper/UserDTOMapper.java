package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentRecord;
import com.sprint.mission.discodeit.dto.userdto.UserRegitrationRecord;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserDTOMapper {
    public static UserResponseDTO userIdToResponse (UUID userId, UserRepository userRepository, UserStatusRepository userStatusRepository) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus userStatus = userStatusRepository.findByID(userId);

        return new UserResponseDTO(user.getId(),
                user.getProfileID(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static User regtoUser(UserRegitrationRecord req, BinaryContentRepository binaryContentRepository){
        UUID profileID = null;
        if(req.binaryContentRecord() != null){
            BinaryContentRecord p = req.binaryContentRecord();
            BinaryContent binaryContent = new BinaryContent(p.contentType(), p.file());
            profileID = binaryContentRepository.save(binaryContent).getId();
        }

        return new User(req.userName(), req.email(), req.password(), profileID);
    }



}
