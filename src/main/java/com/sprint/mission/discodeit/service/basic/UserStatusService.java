package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.userSatus.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.userSatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

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

    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new NoSuchElementException("유저상태가 없습니다."));
        return new UserStatusResponseDto(
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId()
        );
    }

    public List<UserStatusResponseDto> findAll(UUID id){
        return userStatusRepository.findAll()
                .stream()
                .map(userStatus -> new UserStatusResponseDto(
                        userStatus.getCreatedAt(),
                        userStatus.getUpdatedAt(),
                        userStatus.getUserId()
                )).toList();
    }

    public UserStatusResponseDto update(UUID id, UserStatusRequestDto userStatusUpdateDto) {
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new NoSuchElementException("유저상태가 없습니다."));
        userStatus.touch();
        userStatusRepository.save(userStatus);
        return new UserStatusResponseDto(
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId()
        );
    }

    public void updateByUserId(UUID userId) {
        //userId로 특정 User 객체를 업데이트 한다.
        User user = userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("유저가 없습니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(()-> new NoSuchElementException("UserStatus가 없습니다."));
        userStatus.touch();
        user.touchLastSeen();
        userStatusRepository.save(userStatus);
        userRepository.save(user);

    }

    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }

}
