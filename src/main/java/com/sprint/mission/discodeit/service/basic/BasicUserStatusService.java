package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto dto) {
        // 관련된 사용자가 없다면 예외처리
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자는 없습니다"));
        // 해당 사용자가 이미 userStatus를 가지고 있다면 예외처리
        for(UserStatus us : userStatusRepository.findAll().values()){
            if(us.getUserId().equals(user.getId()))
                throw new IllegalStateException("해당 사용자와 연관된 객체가 있습니다");
        }
        // 만약 예외가 없다면 객체 생성
        UserStatus userStatus = new UserStatus(dto.getUserId());
        // 저장
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusResponseDto findUserStatus(UUID id) {
        UserStatus userStatus = getUserStatus(id);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        return userStatusRepository.findAll().values().stream()
                            .map(userStatusMapper::toDto)
                            .toList();
    }

    @Override
    public UserStatusResponseDto update(UUID userId) {
        // 수정할 객체 불러옴
        UserStatus userStatus = getUserStatus(userId);
        // 최신 접속 시간 업데이트
        userStatus.updateAccessTime();
        // 저장
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }

    private UserStatus getUserStatus(UUID id){
        return userStatusRepository.findByUserId(id).orElseThrow(() -> new NoSuchElementException("해당 유저상태가 없습니다."));
    }
}
