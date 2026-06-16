package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.dto.authDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtRegistry jwtRegistry;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "users", allEntries = true)
    public UserDto updateRole(UserRoleUpdateRequest request) {

        User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        // 과거 권한
        Role oldRole = user.getRole();
        user.updateRole(request.getNewRole());
        if(!oldRole.equals(user.getRole())){
            eventPublisher.publishEvent(new RoleUpdatedEvent(user.getId(), oldRole, request.getNewRole()));
        }
        UserDto dto = userMapper.toDto(user, true);
        // 권한 변경 시 세션 만료되도록 함 -> 토큰 기반으로 바뀌었기 때문에 수정
        expireUserSessions(dto);
        log.info("권한 수정으로 세션 만료!");
        return dto;
    }

    @Override
    public boolean isOnline(UserDto dto){
        // SessionRegistry에서 map에 키값을 비교할때 DiscodeitUserDetails의 hashcode와 equals를 오버라이딩하여
        // UserDetails의 Dto의 id값으로 비교하도록 했기 때문에 정확히 dto만 있어도 value를 가져올 수 있음
//        DiscodeitUserDetails principal = new DiscodeitUserDetails(dto,"");
//        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
//        return !sessions.isEmpty();


        // 토큰 기반
        return jwtRegistry.hasActiveJwtInformationByUserId(dto.getId());
    }

    // 토큰 방식으로 추후에 바꾸고 캐시도 비울것
    private void expireUserSessions(UserDto dto){

//        DiscodeitUserDetails principal = new DiscodeitUserDetails(dto,"");
//        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
//        for(SessionInformation session : sessions){
//            session.expireNow();
//        }

        // 토큰기반
        jwtRegistry.invalidateJwtInformationByUserId(dto.getId());
    }
}
