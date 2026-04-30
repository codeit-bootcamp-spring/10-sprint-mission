package com.codeit.mission.deokhugam.notification.event.listener;

import com.codeit.mission.deokhugam.notification.event.CommentRegisteredEvent;
import com.codeit.mission.deokhugam.notification.event.ReviewLikedEvent;
import com.codeit.mission.deokhugam.notification.event.ReviewRankedEvent;
import com.codeit.mission.deokhugam.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationService notificationService;

  @Async
  // 발행자의 트랜잭션이 성공적으로 완료된 후에 이벤트가 처리되도록 함
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleCommentRegisteredEvent(CommentRegisteredEvent event) {
    log.info(
      "[NOTIFICATION_EVENT_LISTENER] CommentRegisteredEvent received: actorId={}, receiverId={}, reviewId={}",
      event.getActorId(), event.getReceiverId(), event.getReviewId());

    notificationService.createByComment(event.getActorId(), event.getReceiverId(),
      event.getReviewId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleReviewLikedEvent(ReviewLikedEvent event) {
    log.info(
      "[NOTIFICATION_EVENT_LISTENER] ReviewLikedEvent received: actorId={}, receiverId={}, reviewId={}",
      event.getActorId(), event.getReceiverId(), event.getReviewId());

    notificationService.createByLike(event.getActorId(), event.getReceiverId(),
      event.getReviewId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleReviewRankedEvent(ReviewRankedEvent event) {
    log.info("[NOTIFICATION_EVENT_LISTENER] ReviewRankedEvent received: reviewIds={}",
      event.getReviewIds());

    notificationService.createByReviewRanked(event.getReviewIds());
  }
}
