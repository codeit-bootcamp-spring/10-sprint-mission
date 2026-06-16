package com.sprint.mission.discodeit.handler.jwt;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.sse.SseDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (request.getCookies() == null) return;

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();

                    try {
                        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) jwtTokenProvider.getAuthentication(refreshToken).getPrincipal();
                        UserDto userDto =  userDetails.getUserDto();

                        userDto.setOnline(false);

                        jwtRegistry.invalidateJwtInformationByUserId(userDto.getId());


                        applicationEventPublisher.publishEvent(new UserUpdatedEvent(
                                List.of(new SseDto(null, "users.updated", userDto))
                        ));

                        Cookie deleteCookie = new Cookie("REFRESH_TOKEN", null); // 값을 비움
                        deleteCookie.setMaxAge(0);
                        deleteCookie.setPath("/");
                        deleteCookie.setHttpOnly(true);

                        response.addCookie(deleteCookie); // 응답에 삭제 명령이 담긴 쿠키를 실어 보냄

                        log.info("로그아웃 완벽 처리 (Registry, 이벤트, 쿠키 삭제 완료). userId: {}", userDto.getId());
                    } catch (Exception e) {
                        log.error("로그아웃 처리 중 에러: {}", e.getMessage());
                    }
                });
    }
}
