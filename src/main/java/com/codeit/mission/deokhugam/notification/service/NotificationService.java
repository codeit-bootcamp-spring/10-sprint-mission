package com.codeit.mission.deokhugam.notification.service;

import com.codeit.mission.deokhugam.notification.dto.request.NotificationRequestQuery;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationUpdateRequest;
import com.codeit.mission.deokhugam.notification.dto.response.CursorPageResponseNotificationDto;
import com.codeit.mission.deokhugam.notification.dto.response.NotificationDto;
import com.codeit.mission.deokhugam.notification.entity.Notification;
import com.codeit.mission.deokhugam.notification.exception.NotificationNotFoundException;
import com.codeit.mission.deokhugam.notification.exception.NotificationNotOwnedException;
import com.codeit.mission.deokhugam.notification.mapper.NotificationMapper;
import com.codeit.mission.deokhugam.notification.repository.NotificationRepository;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.exception.ReviewNotFoundException;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.exception.UserNotFoundException;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;

  private final NotificationMapper notificationMapper;

  // 이벤트 발행자의 트랜잭션이 종료된 후 호출되므로 새로운 트랜잭션을 만들도록 해야함
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createByLike(UUID senderId, UUID receiverId, UUID reviewId) {
    log.trace("[NOTIFICATION] createByLike() called: senderId={}, receiverId={}, reviewId={}",
      senderId, receiverId, reviewId);

    User sender = getUserOrThrow(senderId);
    User receiver = getUserOrThrow(receiverId);
    Review review = getReviewOrThrow(reviewId);

    Notification notification = notificationRepository.save(
      createNotification(receiver, review,
        "[" + sender.getNickname() + "]님이 나의 리뷰를 좋아합니다.")
    );

    log.info("[NOTIFICATION] notification created by like: id={}", notification.getId());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createByComment(UUID senderId, UUID receiverId, UUID reviewId) {
    log.trace("[NOTIFICATION] createByComment() called: senderId={}, receiverId={}, reviewId={}",
      senderId, receiverId, reviewId);

    User sender = getUserOrThrow(senderId);
    User receiver = getUserOrThrow(receiverId);
    Review review = getReviewOrThrow(reviewId);

    Notification notification = notificationRepository.save(
      createNotification(receiver, review,
        "[" + sender.getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.")
    );

    log.info("[NOTIFICATION] notification created by comment: id={}", notification.getId());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createByReviewRanked(List<UUID> reviewIds) {
    log.trace("[NOTIFICATION] createByReviewRanked() called: reviewIds={}", reviewIds);

    List<Review> reviews = reviewRepository.findByIdIn(reviewIds);
    List<Notification> newNotifications = new ArrayList<>();

    // fixme: 인기 리뷰 알림 메시지 예시를 확인할 수 없어서 임시로 작성
    reviews.forEach(review ->
      newNotifications.add(
        createNotification(review.getUser(), review, "나의 리뷰가 인기 리뷰로 등록되었습니다.")
      )
    );

    notificationRepository.saveAll(newNotifications);

    log.info("[NOTIFICATION] notifications created by review ranked: ids={}",
      newNotifications.stream().map(Notification::getId).toList());
  }

  public CursorPageResponseNotificationDto findByUserId(UUID userId,
    NotificationRequestQuery query) {
    log.trace(
      "[NOTIFICATION] findByUserId() called: userId={}, direction={}, cursor={}, after={}, limit={}",
      userId, query.direction(), query.cursor(), query.after(), query.limit());

    Slice<Notification> slice =
      notificationRepository.findByUserWithCursor(userId, query);

    long totalCount = notificationRepository.countByUserId(userId);

    List<Notification> content = slice.getContent();

    String nextCursor = null;
    Instant nextAfter = null;

    if (!content.isEmpty()) {
      Notification last = content.get(content.size() - 1);

      if (slice.hasNext()) {
        nextCursor = last.getCreatedAt().toString();
        nextAfter = last.getCreatedAt();
      }

    }

    List<NotificationDto> dtoContent = slice.getContent()
      .stream()
      .map(notificationMapper::toDto)
      .toList();

    return CursorPageResponseNotificationDto.builder()
      .content(dtoContent)
      .nextCursor(nextCursor)
      .nextAfter(nextAfter)
      .size(content.size())
      .totalElements(totalCount)
      .hasNext(slice.hasNext())
      .build();

  }

  @Transactional
  public NotificationDto updateById(UUID notificationId, UUID requestUserId,
    NotificationUpdateRequest requestDto) {
    log.trace("[NOTIFICATION] updateById() called: notificationId={}, requestUserId={}",
      notificationId, requestUserId);

    Notification notification = getNotificationOrThrow(notificationId);

    validateNotificationOwner(notification, requestUserId);

    notification.updateConfirmed(requestDto.confirmed());

    log.info("[NOTIFICATION] updated notification id: id={}", notification.getId());

    return notificationMapper.toDto(notification);
  }

  @Transactional
  public void updateByUserId(UUID userId) {
    log.trace("[NOTIFICATION] updateByUserId() called: userId={}", userId);

    validateUserExists(userId);
    int updatedCount = notificationRepository.updateAllAsConfirmed(userId);

    log.info("[NOTIFICATION] updated notification: userId={}, updatedCount={}", userId,
      updatedCount);
  }

  private Notification createNotification(
    User receiver,
    Review review,
    String message
  ) {
    return Notification.builder()
      .reviewContent(review.getContent())
      .message(message)
      .confirmed(false)
      .user(receiver)
      .review(review)
      .build();
  }

  // 요청자의 id와 알림을 받은 사람의 id를 대조
  private void validateNotificationOwner(Notification notification, UUID requestUserId) {
    if (!requestUserId.equals(notification.getUser().getId())) {
      throw new NotificationNotOwnedException();
    }
  }

  // 실제로 존재하는 유저 id인지 확인
  private void validateUserExists(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private Review getReviewOrThrow(UUID reviewId) {
    return reviewRepository.findById(reviewId)
      .orElseThrow(() -> new ReviewNotFoundException(reviewId));
  }

  private Notification getNotificationOrThrow(UUID notificationId) {
    return notificationRepository.findById(notificationId)
      .orElseThrow(() -> new NotificationNotFoundException(notificationId));
  }
}
