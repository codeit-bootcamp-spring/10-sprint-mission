package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService{
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper mapper;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request){
        request.validate();

        //관련 User 없으면 예외
        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User가 없습니다."));
        // 같은 User 관련 객체 이미 있으면 예외
        boolean exists = userStatusRepository.findAll().stream()
                .anyMatch(us -> us.getUserId().equals(request.userId()));
        if(exists){
            throw new IllegalArgumentException("이미 해당 유저의 UserStatus가 존재합니다.");
        }
        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);

        return mapper.toResponse(userStatus);
    }
    @Override
    public UserStatusResponse findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        UserStatus us = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus가 없습니다: " + id));
        return mapper.toResponse(us);
    }
    @Override
    public List<UserStatusResponse> findAll(){
        return userStatusRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        request.validate();

        UserStatus us = userStatusRepository.findById(request.userStatusId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus가 없습니다: " + request.userStatusId()));

        // 수정 값 반영
        if (request.refreshLogin()) {
            us.refreshLogin();
        }

        userStatusRepository.save(us);
        return mapper.toResponse(us);
    }
    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request){
        request.validate();

        // 유저 존재 확인
        userRepository.findById(request.userId())
                .orElseThrow(()->new NoSuchElementException("유저가 없습니다."));

        // userId로 status 찾기
        UserStatus us = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(()->new NoSuchElementException("해당유저의 userStatus가 없습니다: " + request.userId()));

        if(request.refreshLogin()) us.refreshLogin();

        userStatusRepository.save(us);
        return mapper.toResponse(us);
    }


    @Override
    public void delete(UUID id){
        if(id==null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        userStatusRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("삭제할 User가 없습니다."));
        userStatusRepository.delete(id);
    }



    }




