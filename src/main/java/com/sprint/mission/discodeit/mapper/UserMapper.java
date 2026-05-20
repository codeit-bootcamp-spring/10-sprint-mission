package com.sprint.mission.discodeit.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

	private SessionRegistry sessionRegistry;

	@Autowired
	protected void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

	@Mapping(target = "online", expression = "java(isOnline(user))")
	public abstract UserDto toDto(User user);

	protected Boolean isOnline(User user) {
		if (user == null || user.getId() == null) {
			return false;
		}

		UUID userId = user.getId();
		return sessionRegistry.getAllPrincipals().stream()
			.filter(DiscodeitUserDetails.class::isInstance)
			.map(DiscodeitUserDetails.class::cast)
			.filter(principal -> userId.equals(principal.getUserDto().id()))
			.anyMatch(principal -> sessionRegistry.getAllSessions(principal, false).stream()
				.anyMatch(sessionInformation -> !sessionInformation.isExpired()));
	}
}
