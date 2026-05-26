package com.sprint.mission.discodeit.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenCookieManager refreshTokenCookieManager;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		DiscodeitUserDetails userDetails = (DiscodeitUserDetails)authentication.getPrincipal();
		UserDto userDto = markOnline(userDetails.getUserDto());
		String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
		String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
		JwtDto jwtDto = new JwtDto(userDto, accessToken);

		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieManager.create(request, refreshToken).toString());
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), jwtDto);
	}

	private UserDto markOnline(UserDto userDto) {
		return new UserDto(
			userDto.id(),
			userDto.username(),
			userDto.email(),
			userDto.profile(),
			true,
			userDto.role()
		);
	}
}
