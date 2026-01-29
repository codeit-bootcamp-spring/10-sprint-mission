package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    // 계정 생성
    @Override
    public UserResponse createAccount(UserCreateRequest request){
        throw new UnsupportedOperationException("작업 예정");
    }

    // 단건 조회
    @Override
    public UserResponse getAccountById(UUID id) {
        throw new UnsupportedOperationException("작업 예정");
    }

    // 전체 조회
    @Override
    public List<UserResponse> getAllAccounts() {
        throw new UnsupportedOperationException("작업 예정");
    }

    // 계정 정보 수정
    @Override
    public UserResponse updateAccount(UUID id, UserUpdateRequest request){
        throw new UnsupportedOperationException("작업 예정");
    }

    // 계정 삭제
    @Override
    public void deleteAccount(UUID id) {
        throw new UnsupportedOperationException("작업 예정");
    }
}
