package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.ChannelNoMemberEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    //
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    //
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserDto.Response create(UserDto.CreateRequest request) {
        String username = request.username();
        String email = request.email();
        validateUser(null, username, email);
        UUID profileId = request.profileId();
        String password = passwordEncoder.encode(request.password());

        User user = new User(username, email, password, profileId);
        User createdUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(createdUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);
        return toDto(createdUser);
    }

    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request) {
        User user=  userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        validateUser(user, request.newUsername(), request.newEmail());

        String encodedPassword = (request.newPassword() != null) // null일 때 암호화해서 null이 아니게 되는 것을 방지
                ? passwordEncoder.encode(request.newPassword())
                : null;

        if (Boolean.TRUE.equals(request.isProfileDeleted())) {
            user.deleteProfile(); // 여기서 this.profileId = null; 실행
        }

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(request.newUsername(), request.newEmail(), encodedPassword, request.profileId());
        User updatedUser = userRepository.save(user);

        return toDto(updatedUser);
    }

    // 트랜잭션은 aggregate 단위로만 묶는다.
    // 현재 user는 userStatus없이 생존할 수 있으므로 트랜잭션이 부적절할 수 있다.
    // @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        deleteMemberInPrivate(userId);

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    // validation
    private void validateUser(User user, String username, String email) {
        // 유저가 null이면 create, 있으면 update
        // 유저가 null이 아닌 경우만(update면) 값이 변경되었는지 equal로 체크해서 변경되지 않았으면 스킵
        if (email != null
                && userRepository.existsByEmail(email)
                && (user == null || !user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (username != null
                && userRepository.existsByUsername(username)
                && (user == null || !user.getUsername().equals(username))) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }
    }

    // Helper
    public UserDto.Response toDto(User user) {
        // 현재는 요구사항에서 find, findAll이 유저 상태 정보를 같이 포함하라고 해서 강하게 결합했음
        // 유저 상태 정보가 손실되어도 유저는 정상적으로 작동해야 하므로 실제로는 예외 처리를 하면 안됨
        // 트랜잭션으로 정합성을 보장하거나, 손실되면 offline으로 유연하게 처리하도록 바꿔야 할 필요성 존재
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저의 상태가 없습니다:" + user.getId()));

        return userMapper.toResponse(user, userStatus.isOnline());
    }

    // 요구사항 외 구현
    // 비공개 채널의 멤버가 삭제될 때
    private void deleteMemberInPrivate(UUID userId) {
        List<ReadStatus> userReadStatuses = readStatusRepository.findAllByUserId(userId);

        for (ReadStatus rs : userReadStatuses) {
            UUID channelId = rs.getChannelId();

            List<ReadStatus> channelMembers = readStatusRepository.findAllByChannelId(channelId);

            if (channelMembers.size() <= 1) { // 채널에 자신만 존재
                eventPublisher.publishEvent(new ChannelNoMemberEvent(channelId));
            } else {
                readStatusRepository.deleteById(rs.getId());
            }
        }
    }
}
