package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notificationdto.NotificationDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  // 알림 레포지토리에서 현재 유저가 가진 알림들을 List 형태로 가져오는 메서드
  @Override
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "notificationsByUser", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().name"
  )
  public List<NotificationDto> findAllByCurrentUser() {
    UUID currentUserId = currentUserId(); // SecurityContextHolder를 통해 현재 로그인 된 User Id를 추출
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(currentUserId).stream()
        .map(this::toDto)
        .toList();
  }

  // 알림 ID를 통해 알림을 삭제하는 메서드
  @Override
  @Transactional
  @CacheEvict(cacheNames = "notificationsByUser", allEntries = true)
  public void delete(UUID notificationId) {
    UUID currentUserId = currentUserId(); // 현재 유저 id 추출

    // 알림 객체를 레포지토리로부터 가져옴
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId)); // 404

    if (!notification.getReceiver().getId().equals(currentUserId)) {
      throw new AccessDeniedException("다른 유저의 알림을 삭제할 수 없음"); // 403 exception
    }

    // 해당 알림 삭제
    notificationRepository.delete(notification);
  }

  // Notification 객체를 Dto 형태로 매핑 및 반환
  private NotificationDto toDto(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent()
    );
  }

  // SecurityContextHolder에서 현재 유저의 id를 추출
  private UUID currentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      throw new AuthenticationCredentialsNotFoundException("Authentication이 유효하지 않습니다.");
    }
    return userDetails.getUserDto().id();
  }
}
