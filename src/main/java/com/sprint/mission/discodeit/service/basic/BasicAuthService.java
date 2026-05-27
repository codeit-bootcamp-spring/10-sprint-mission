//package com.sprint.mission.discodeit.service.basic;
//
//import com.sprint.mission.discodeit.dto.UserDto;
//import com.sprint.mission.discodeit.dto.LoginRequest;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.exception.user.InvalidCredentialException;
//import com.sprint.mission.discodeit.mapper.UserMapper;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//import com.sprint.mission.discodeit.service.AuthService;
//import jakarta.transaction.Transactional;
//import java.time.Instant;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//public class BasicAuthService implements AuthService {
//
//  private final UserRepository userRepository;
//  private final UserStatusRepository userStatusRepository;
//  private final UserMapper userMapper;
//
/// /  @Override /  @Transactional /  public UserDto login(LoginRequest request) { /    User user =
/// userRepository.findByUsername(request.username()) /        .filter(u ->
/// u.getPassword().equals(request.password())) /        .orElseThrow(() -> new
/// InvalidCredentialException(request.username())); / /    UserStatus userStatus =
/// userStatusRepository.findByUserId(user.getId()) /        .orElseGet(() -> { /
/// UserStatus newUserStatus = new UserStatus(user, Instant.now()); /          return
/// userStatusRepository.save(newUserStatus); /        }); / /    userStatus.update(Instant.now());
/// /    return userMapper.toUserDto(user); /  }
//}
