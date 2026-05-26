package com.sprint.mission.discodeit.handler.jwt;

import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = null;

        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("REFRESH_TOKEN".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if(refreshToken != null){
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token ->{
                        refreshTokenRepository.delete(token);
                        log.info("로그아웃! DB에서 refreshToken 삭제 완료");
                            });
        }

        // Cookie에 수명 0으로 주입해서 삭제 처리함
        Cookie deleteCookie = new Cookie("REFRESH_TOKEN", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");

        response.addCookie(deleteCookie);
    }
}
