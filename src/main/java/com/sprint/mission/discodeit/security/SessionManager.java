package com.sprint.mission.discodeit.security;

import java.util.UUID;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SessionManager {

	private final SessionRegistry sessionRegistry;

	public void invalidateSessionsByUserId(UUID userId) {
		sessionRegistry.getAllPrincipals().stream()
			.filter(DiscodeitUserDetails.class::isInstance)
			.map(DiscodeitUserDetails.class::cast)
			.filter(principal -> userId.equals(principal.getUserDto().id()))
			.forEach(principal -> sessionRegistry.getAllSessions(principal, false).stream()
				.map(SessionInformation::getSessionId)
				.forEach(this::expireSession));
	}

	private void expireSession(String sessionId) {
		SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
		if (sessionInformation != null) {
			sessionInformation.expireNow();
		}
	}
}
