package com.sprint.mission.discodeit.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry jwtRegistry;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String accessToken = resolveAccessToken(request);

		if (StringUtils.hasText(accessToken) && SecurityContextHolder.getContext().getAuthentication() == null) {
			authenticate(request, accessToken);
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(HttpServletRequest request, String accessToken) {
		try {
			if (!jwtTokenProvider.validateAccessToken(accessToken)) {
				return;
			}
			if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
				return;
			}

			String username = jwtTokenProvider.getUsername(accessToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (JwtTokenException | UsernameNotFoundException exception) {
			SecurityContextHolder.clearContext();
		}
	}

	private String resolveAccessToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			return null;
		}
		return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
	}
}
