package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

/**
 * 도메인 이벤트에 따른 알림 발송 정책을 담당하는 서비스입니다.
 */
public interface NotificationEventService {
  void sendByMessageCreated(UUID messageId);
  void sendByRoleUpdated(UUID userId, Role oldRole, Role newRole);
  void sendAsyncErrorNotification(String requestId, String methodName, String errorMessage);
}
