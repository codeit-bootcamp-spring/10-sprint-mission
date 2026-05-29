package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

	private JwtRegistry jwtRegistry;

	@Autowired
	protected void setJwtRegistry(JwtRegistry jwtRegistry) {
		this.jwtRegistry = jwtRegistry;
	}

	@Mapping(target = "online", expression = "java(isOnline(user))")
	public abstract UserDto toDto(User user);

	protected Boolean isOnline(User user) {
		if (user == null || user.getId() == null) {
			return false;
		}

		return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
	}
}
