package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.authDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRole(UserRoleUpdateRequest request) {

        User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        user.updateRole(request.getNewRole());
        eventPublisher.publishEvent(new RoleUpdatedEvent(user.getId(), user.getRole(), request.getNewRole()));
        UserDto dto = userMapper.toDto(user, true);
        // 권한 변경 시 세션 만료되도록 함
        expireUserSessions(dto);
        log.info("권한 수정으로 세션 만료!");
        return dto;
    }

    @Override
    public boolean isOnline(UserDto dto){
        // SessionRegistry에서 map에 키값을 비교할때 DiscodeitUserDetails의 hashcode와 equals를 오버라이딩하여
        // UserDetails의 Dto의 id값으로 비교하도록 했기 때문에 정확히 dto만 있어도 value를 가져올 수 있음
        DiscodeitUserDetails principal = new DiscodeitUserDetails(dto,"");
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);

        return !sessions.isEmpty();
    }

    private void expireUserSessions(UserDto dto){

        DiscodeitUserDetails principal = new DiscodeitUserDetails(dto,"");
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
        for(SessionInformation session : sessions){
            session.expireNow();
        }
    }
}
