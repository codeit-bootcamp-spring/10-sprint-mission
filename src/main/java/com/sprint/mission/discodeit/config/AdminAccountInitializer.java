package com.sprint.mission.discodeit.config;

import java.time.Instant;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("!test")
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements ApplicationRunner {

	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_EMAIL = "admin@discodeit.com";
	private static final String ADMIN_PASSWORD = "admin";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {
		if (userRepository.existsByRole(Role.ADMIN)) {
			log.info("[ADMIN_ACCOUNT_INIT] ADMIN 계정이 이미 존재합니다.");
			return;
		}

		User adminUser = userRepository.findByUsername(ADMIN_USERNAME)
			.or(() -> userRepository.findByEmail(ADMIN_EMAIL))
			.map(user -> {
				user.updateRole(Role.ADMIN);
				return user;
			})
			.orElseGet(() -> {
				User user = new User(
					ADMIN_USERNAME,
					ADMIN_EMAIL,
					passwordEncoder.encode(ADMIN_PASSWORD),
					null,
					Role.ADMIN
				);
				UserStatus userStatus = new UserStatus(user, Instant.now());
				return userRepository.save(user);
			});

		log.info("[ADMIN_ACCOUNT_INIT] ADMIN 계정 초기화 완료: userId={}, username={}",
			adminUser.getId(), adminUser.getUsername());
	}
}
