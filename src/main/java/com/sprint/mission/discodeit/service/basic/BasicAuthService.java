package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @Override
    @Transactional
    public UserDTO updateRole(RoleUpdateRequest request) {
        // user 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        // role update , 동시에 같은 유저의 role을 바꾸는 상황은 X
        user.updateRole(request.newRole());

        // 역할 변경된 유저의 활성 모든 세션 만료 및 재로그인
        sessionRegistry.getAllPrincipals().stream()
                .map(p -> (DiscodeitUserDetails) p)
                .filter(p -> p.getUserDTO().id().equals(user.getId()))
                .flatMap(p -> sessionRegistry.getAllSessions(p, false).stream())
                .forEach(SessionInformation::expireNow);

        // 명시적 save X, find로 영속성 컨텍스트에 user가 올라있기 때문
        return userMapper.toDTO(user);
    }
}
