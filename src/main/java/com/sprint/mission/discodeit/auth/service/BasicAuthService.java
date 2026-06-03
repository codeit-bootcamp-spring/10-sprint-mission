package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.common.exception.auth.TokenInvalidException;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.jwt.JwtInformation;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.user.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;
  //private final SessionRegistry sessionRegistry;

  @Override
  public JwtDto refresh(String refreshToken, HttpServletResponse response) {
    if (refreshToken == null) {
      throw new TokenInvalidException();
    }
    Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
    String username = (String) claims.get("sub");
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    Map<String, Object> newClaims = new HashMap<>();
    newClaims.put("roles", claims.get("roles"));

    String newAccessToken = jwtTokenProvider.generateToken(newClaims, username);
    String rotationToken = jwtTokenProvider.generateRefreshToken(username);

    DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) userDetails;
    UserDto userDto = discodeitUserDetails.getUserDto();

    JwtInformation newJwtInformation = new JwtInformation(userDto, newAccessToken, rotationToken);
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", rotationToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    response.addCookie(refreshCookie);

    return new JwtDto(userDto, newAccessToken);
  }

//  @Override
//  public void expireUserSession(UUID userId) {
//    List<Object> principals = sessionRegistry.getAllPrincipals();
//
//    for (Object principal : principals) {
//      if (principal instanceof DiscodeitUserDetails) {
//        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) principal;
//        if (userDetails.getUserDto().id().equals(userId)) {
//          List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
//          for (SessionInformation session : sessions) {
//            session.expireNow();
//          }
//        }
//      }
//    }
//  }
}
