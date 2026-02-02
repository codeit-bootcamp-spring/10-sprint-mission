package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userSatus.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.userSatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserStatusResponseDto create(UserStatusRequestDto userStatusCreateDto) {
        if(!userRepository.existsById(userStatusCreateDto.userId())){
            throw new NoSuchElementException("User가 없습니다.");
        }
        if(userStatusRepository.existsByUserId(userStatusCreateDto.userId())){
            throw new NoSuchElementException("UserStatus가 이미 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(userStatusCreateDto.userId());
        userStatusRepository.save(userStatus);

        return new UserStatusResponseDto(
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId()
        );
    }

}
