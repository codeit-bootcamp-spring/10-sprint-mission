//package com.sprint.mission.discodeit.event.listener;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.Notification;
//import com.sprint.mission.discodeit.entity.ReadStatus;
//import com.sprint.mission.discodeit.entity.Role;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
//import com.sprint.mission.discodeit.event.MessageCreatedEvent;
//import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
//import com.sprint.mission.discodeit.exception.DiscodeitException;
//import com.sprint.mission.discodeit.exception.ErrorCode;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.NotificationRepository;
//import com.sprint.mission.discodeit.repository.ReadStatusRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationRequiredEventListener {
//
//  private final MessageRepository messageRepository;
//  private final ReadStatusRepository readStatusRepository;
//  private final NotificationRepository notificationRepository;
//  private final UserRepository userRepository;
//  private final CacheManager cacheManager;
//
//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @EventListener
//  public void on(MessageCreatedEvent event) {
//
//    try {
//      Message message = messageRepository.findWithAuthorAndChannelById(event.messageId())
//          .orElseThrow();
//
//      Channel channel = message.getChannel();
//
//      List<ReadStatus> targets =
//          readStatusRepository.findAllByChannelIdAndNotificationEnabledTrueAndUserIdNot(
//              channel.getId(),
//              message.getAuthor().getId()
//          );
//
//      List<Notification> notifications = targets.stream()
//          .map(rs -> new Notification(
//              rs.getUser(),
//              "보낸 사람 (" + channel.getName() + ")",
//              message.getContent()
//          ))
//          .toList();
//
//      notificationRepository.saveAll(notifications);
//
//      Cache cache = cacheManager.getCache("userNotifications");
//
//      if (cache != null) {
//        targets.forEach(
//            rs -> cache.evict(rs.getUser().getId())
//        );
//      }
//
//      log.info("[SUCCESS] Message Notification Created: messageId={}", event.messageId());
//
//    } catch (Exception e) {
//      log.error("[FAIL] Message Notification Failed: messageId={}", event.messageId(), e);
//    }
//  }
//
//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @EventListener
//  public void on(RoleUpdatedEvent event) {
//
//    try {
//      Notification notification = new Notification(
//          userRepository.findById(event.userId()).orElseThrow(),
//          "권한이 변경되었습니다.",
//          event.previousRole() + " -> " + event.newRole()
//      );
//
//      notificationRepository.save(notification);
//
//      Cache cache = cacheManager.getCache("userNotifications");
//
//      if (cache != null) {
//        cache.evict(event.userId());
//      }
//
//      log.info("[SUCCESS] Role Notification Created: userId={}", event.userId());
//
//    } catch (Exception e) {
//      log.error("[FAIL] Role Notification Failed: userId={}", event.userId(), e);
//    }
//  }
//
//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @EventListener
//  public void on(BinaryUploadFailedEvent event) {
//    try {
//      User admin = userRepository.findByRole(Role.ADMIN)
//          .orElseThrow(() -> new DiscodeitException(ErrorCode.ADMIN_USER_NOT_FOUND));
//
//      Notification notification = new Notification(
//          admin,
//          "S3 파일 업로드 실패",
//          """
//              RequestId: %s
//
//              BinaryContentId: %s
//
//              Error: %s
//              """
//              .formatted(
//                  event.requestId(),
//                  event.binaryContentId(),
//                  event.errorMessage()
//              )
//      );
//
//      notificationRepository.save(notification);
//
//      Cache cache = cacheManager.getCache("userNotifications");
//
//      if (cache != null) {
//        cache.evict(admin.getId());
//      }
//
//      log.info(
//          "[SUCCESS] Binary Upload Failure Notification Created: binaryContentId={}",
//          event.binaryContentId()
//      );
//
//    } catch (Exception e) {
//      log.error(
//          "[FAIL] Binary Upload Failure Notification Failed: binaryContentId={}",
//          event.binaryContentId(),
//          e
//      );
//    }
//  }
//}
