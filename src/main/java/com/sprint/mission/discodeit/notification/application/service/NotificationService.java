package com.sprint.mission.discodeit.notification.application.service;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationErrorCode;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationException;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationUserResolver;
import com.sprint.mission.discodeit.notification.infra.repository.NotificationRepository;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationMapper;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository repository;
  private final NotificationUserResolver userResolver;
  private final NotificationMapper mapper;

  public void create(List<UUID> receiverIds, String title, String content) {
    List<User> receivers = userResolver.getProxyOrThrow(receiverIds);
    List<Notification> notifications = mapper.toEntityFrom(receivers, title, content);
    repository.saveAll(notifications);
  }

  public List<NotificationDto> find(UUID receiverId) {
    return mapper.toDto(repository.findAllByReceiver_Id(receiverId));
  }

  public void delete(UUID id) {
    DomainServiceSupport.deleteOrThrow(id, repository,
        value -> new NotificationException(NotificationErrorCode.NOT_FOUND, id));
  }

  public void sendToAdmin(String title, List<String> messages) {
    User admin = userResolver.getProxyByUsername("admin");
    List<Notification> notifications = messages.stream()
        .map(message -> mapper.toEntityFrom(admin, title, message))
        .toList();
    repository.saveAll(notifications);
  }
}
