package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ConditionalOnProperty(name = "admin.enable", havingValue = "true")
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final UserService userService;

  @Value("${admin.email:admin@admin.com}")
  private String email;
  @Value("${admin.username:admin}")
  private String username;
  @Value("${admin.password:admin1234!}")
  private String password;
  @Value("${admin.profile.byte:#{null}}")
  private String profileByte;
  @Value("${admin.profile.type:jpeg}")
  private String contentType;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.info("✅ 관리자 계정이 존재합니다. 관리자 계정 생성을 건너뜁니다.");
      return;
    }
    Optional<BinaryContentCreateDto> profile = Optional.empty();
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    if (profileByte != null && !profileByte.isEmpty()) {
      String base64Text = profileByte.trim();

      try {
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Text);

        BinaryContentCreateDto binaryDto = new BinaryContentCreateDto(
            "admin-profile." + contentType,
            "image/" + contentType,
            decodedBytes,
            (long) decodedBytes.length
        );
        profile = Optional.of(binaryDto);
      } catch (IllegalArgumentException e) {
        // 만약 디코딩에 실패한다면(Base64 형식이 아니라면) 그대로 사용하거나 에러 처리
        log.error("⛔️ 어드민 프로필 디코딩 실패");
      }
    }

    try {
      UserDto userDto = userService.create(request, profile);
      User user = userRepository.findById(userDto.id()).orElseThrow();
      user.updateRole(Role.ADMIN);
      log.info("✅ 관리자 계정이 생성되었습니다. username={}", username);
    } catch (Exception e) {
      log.error("⛔️ 관리자 계정 생성 실패: ", e);
      log.error("⛔️관리자 계정 생성 실패로 프로그램을 종료합니다.");
      throw e;
    }

  }
}
