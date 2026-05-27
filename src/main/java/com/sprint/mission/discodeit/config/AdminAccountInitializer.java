package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("!test")
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${discodeit.admin.username}")
	private String adminUsername;

	@Value("${discodeit.admin.email}")
	private String adminEmail;

	@Value("${discodeit.admin.password}")
	private String adminPassword;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {
		validateAdminProperties();

		if (userRepository.existsByRole(Role.ADMIN)) {
			log.info("[ADMIN_ACCOUNT_INIT] ADMIN 계정이 이미 존재합니다.");
			return;
		}

		User adminUser = userRepository.findByUsername(adminUsername)
			.or(() -> userRepository.findByEmail(adminEmail))
			.map(user -> {
				user.updateRole(Role.ADMIN);
				return user;
			})
			.orElseGet(() -> {
				User user = new User(
					adminUsername,
					adminEmail,
					passwordEncoder.encode(adminPassword),
					null,
					Role.ADMIN
				);
				return userRepository.save(user);
			});

		log.info("[ADMIN_ACCOUNT_INIT] ADMIN 계정 초기화 완료: userId={}, username={}",
			adminUser.getId(), adminUser.getUsername());
	}

	private void validateAdminProperties() {
		if (!StringUtils.hasText(adminUsername)) {
			throw new IllegalStateException("ADMIN_USERNAME 또는 discodeit.admin.username 설정이 필요합니다.");
		}
		if (!StringUtils.hasText(adminEmail)) {
			throw new IllegalStateException("ADMIN_EMAIL 또는 discodeit.admin.email 설정이 필요합니다.");
		}
		if (!StringUtils.hasText(adminPassword)) {
			throw new IllegalStateException("ADMIN_PASSWORD 또는 discodeit.admin.password 설정이 필요합니다.");
		}
	}
}
