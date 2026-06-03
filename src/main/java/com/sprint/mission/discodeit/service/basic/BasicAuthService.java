package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final com.sprint.mission.discodeit.security.DiscodeitUserDetailsService userDetailsService;

    @Override
    @Transactional
    public UserDTO updateRole(RoleUpdateRequest request) {
        // user 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        // role update , 동시에 같은 유저의 role을 바꾸는 상황은 X
        user.updateRole(request.newRole());

        // 명시적 save X, find로 영속성 컨텍스트에 user가 올라있기 때문
        return userMapper.toDTO(user);
    }

    @Override
    public JwtDto refresh(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        // 토큰에서 userId 추출 후 사용자 조회
        java.util.UUID userId = jwtTokenProvider.getUserId(refreshToken);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserById(userId);
        var user = userDetails.getUserDTO();

        // 새 액세스 토큰 + 새 리프레시 토큰 발급 (Rotation)
        String newAccessToken = jwtTokenProvider.generateToken(user.id(), user.username(), user.role().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        log.debug("토큰 재발급 성공 - userId: {}", userId);
        return new JwtDto(user, newAccessToken, newRefreshToken);
    }
}
