package com.sprint.mission.discodeit.security.session;

import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 사용자 세션 관리 클래스
@Component
@Slf4j
@RequiredArgsConstructor
public class UserSessionManager {

    private final SessionRegistry sessionRegistry;

    // 사용자 온라인 여부 확인
    public boolean isOnline(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException("userId", null);
        }

        return getDiscodeitUserDetailsStream()
                .filter(discodeitUserDetails ->
                        isEqualUserId(discodeitUserDetails, userId)
                )
                .anyMatch(discodeitUserDetails ->
                        haveActiveSession(discodeitUserDetails)
                );
    }

    public Set<UUID> getOnlineUserIds() {
        return getDiscodeitUserDetailsStream()
                .filter(discodeitUserDetails ->
                        haveActiveSession(discodeitUserDetails)
                )
                // 유효한 세션
                .map(discodeitUserDetails ->
                        discodeitUserDetails.getUserDto().id()
                )
                .collect(Collectors.toSet());
    }

    // 권한이 변경된 사용자의 로그인 세션 만료 처리
    public void expiredUserSession(UUID userId) {
        getDiscodeitUserDetailsStream()
                // 권한이 변경된 사용자와 동일한 userId를 가진 DiscodeitUserDetails 찾기
                .filter(discodeitUserDetails ->
                        isEqualUserId(discodeitUserDetails, userId)
                )
                // 찾은 principal의 만료되지 않은 세션 목록 조회 후 만료 처리
                .forEach(discodeitUserDetails ->
                        // 해당 사용자의 DiscodeitUserDetails의 세션 목록을 가져옴 (false -> 이미 만료된 세션 제외)
                        sessionRegistry.getAllSessions(discodeitUserDetails, false)
                                .forEach(sessionInformation -> {
                                            // 세션 만료 처리
                                            sessionInformation.expireNow();

                                            log.debug("[SESSION_EXPIRED] 권한 변경으로 세션 만료: userId={}, sessionId={}",
                                                    userId, sessionInformation.getSessionId());
                                        }
                                )
                );

    }

    // SessionRegistry에 저장된 principal 중 DiscodeitUserDetails만 조회
    private Stream<DiscodeitUserDetails> getDiscodeitUserDetailsStream() {
        return sessionRegistry.getAllPrincipals().stream()
                // SessionRegistry에 등록된 principal 중 DiscodeitUserDetails만 필터링
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                // Object를 DiscodeitUserDetails(principal)로 형변환
                .map(principal -> (DiscodeitUserDetails) principal);
    }

    // 권한이 변경된 사용자와 동일한 userId를 가진 DiscodeitUserDetails 찾기
    private boolean isEqualUserId(DiscodeitUserDetails discodeitUserDetails, UUID userId) {
        return discodeitUserDetails.getUserDto().id().equals(userId);
    }

    // 만료되지 않은 세션 목록이 비어있지 않은가? = 유효한 세션이 하나라도 존재하는가? => 존재하면 true
    private boolean haveActiveSession(DiscodeitUserDetails discodeitUserDetails) {
        return !sessionRegistry.getAllSessions(discodeitUserDetails, false)
                .isEmpty();
    }
}
