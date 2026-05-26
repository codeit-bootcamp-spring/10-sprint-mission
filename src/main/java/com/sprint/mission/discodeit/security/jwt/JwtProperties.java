package com.sprint.mission.discodeit.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "discodeit.security.jwt")
public class JwtProperties {

	private String secret;
	private String issuer = "discodeit";
	private long accessTokenValiditySeconds = 1800;
	private long refreshTokenValiditySeconds = 1209600;
}
