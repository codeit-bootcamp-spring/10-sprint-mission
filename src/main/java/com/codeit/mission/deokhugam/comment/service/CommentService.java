package com.codeit.mission.deokhugam.comment.service;

import com.codeit.mission.deokhugam.comment.dto.request.CommentCreateRequest;
import com.codeit.mission.deokhugam.comment.dto.request.CommentFindAllRequest;
import com.codeit.mission.deokhugam.comment.dto.request.CommentUpdateRequest;
import com.codeit.mission.deokhugam.comment.dto.response.CommentDto;
import com.codeit.mission.deokhugam.comment.dto.response.CursorPageResponseCommentDto;
import com.codeit.mission.deokhugam.comment.entity.Comment;
import com.codeit.mission.deokhugam.comment.entity.CommentStatus;
import com.codeit.mission.deokhugam.comment.exception.CommentAuthorException;
import com.codeit.mission.deokhugam.comment.exception.CommentNotFoundException;
import com.codeit.mission.deokhugam.comment.mapper.CommentMapper;
import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.notification.event.CommentRegisteredEvent;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.exception.ReviewNotFoundException;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.entity.UserStatus;
import com.codeit.mission.deokhugam.user.exception.UserNotFoundException;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final CommentMapper commentMapper;

  private final ApplicationEventPublisher eventPublisher;

  // 댓글 등록
  @Transactional
  public CommentDto createComment(CommentCreateRequest request) {
    // Review 조회
    Review review = getReviewOrThrow(request.reviewId());
    // Review 상태 검증
    validReviewStatus(review);

    // User 조회
    User user = getUserOrThrow(request.userId());
    // User 상태 검증
    validUserStatus(user);

    Comment comment = Comment.builder()
        .reviewId(request.reviewId())
        .userId(request.userId())
        .content(request.content())
        .status(CommentStatus.ACTIVE)
        .build();

    Comment savedComment = commentRepository.saveAndFlush(comment);
    reviewRepository.incrementCommentCount(review.getId());

    log.info("[COMMENT_CREATE] Created Comment: id={}", savedComment.getId());

    // 댓글 등록 이벤트 발행
    if (review.getUser() != null) {
      eventPublisher.publishEvent(
          new CommentRegisteredEvent(user.getId(), review.getUser().getId(), review.getId())
      );
    }

    return commentMapper.toDto(savedComment, user.getNickname());
  }

  // 댓글 수정
  @Transactional
  public CommentDto updateComment(UUID commentId, UUID requestUserId,
      CommentUpdateRequest request) {
    // 요청자 조회
    User user = getUserOrThrow(requestUserId);
    // 요청자 상태 검증
    validUserStatus(user);

    // 댓글 조회
    Comment comment = getCommentOrThrow(commentId);
    // 댓글 상태 검증
    validCommentStatus(comment);

    if (!comment.getUserId().equals(requestUserId)) {
      throw new CommentAuthorException();
    }

    comment.updateContent(request.content());
    Comment updatedComment = commentRepository.save(comment);

    log.info("[COMMENT_UPDATE] Updated Comment: id={}", updatedComment.getId());

    return commentMapper.toDto(updatedComment, user.getNickname());
  }

  // 댓글 상세 조회
  @Transactional(readOnly = true)
  public CommentDto findComment(UUID commentId) {
    // 댓글 조회
    Comment comment = getCommentOrThrow(commentId);
    // 댓글 상태 검증
    validCommentStatus(comment);

    String userNickName = getUserOrThrow(comment.getUserId()).getNickname();
    return commentMapper.toDto(comment, userNickName);
  }

  // 댓글 목록 조회
  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findAllComments(CommentFindAllRequest request) {
    // 리뷰 상태 검증
    validReviewStatus(getReviewOrThrow(request.reviewId()));

    int limit = request.limit();
    List<Comment> comments = commentRepository.findAllByCursor(request);

    boolean hasNext = comments.size() > limit;
    if (hasNext) {
      comments = comments.subList(0, limit);
    }

    Map<UUID, String> nicknameMap = userRepository.findAllById(
            comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .toList()
        ).stream()
        .collect(java.util.stream.Collectors.toMap(
            User::getId,
            User::getNickname
        ));

    List<CommentDto> content = comments.stream()
        .map(comment -> commentMapper.toDto(
            comment,
            nicknameMap.get(comment.getUserId())
        ))
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext && !comments.isEmpty()) {
      Comment last = comments.get(comments.size() - 1);
      nextCursor = last.getId().toString();
      nextAfter = last.getCreatedAt();
    }

    long totalElements = commentRepository.countByReviewIdAndStatus(request.reviewId(),
        CommentStatus.ACTIVE);

    return new CursorPageResponseCommentDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        totalElements,
        hasNext
    );
  }

  // 댓글 논리 삭제
  @Transactional
  public void softDelete(UUID commentId, UUID requestUserId) {
    // 댓글 조회
    Comment comment = getCommentOrThrow(commentId);
    // 댓글 상태 검증
    validCommentStatus(comment);

    // 요청자 조회 및 상태 검증
    validUserStatus(getUserOrThrow(requestUserId));

    // 요청자와 댓글의 작성자 ID 비교
    validAuthor(comment, requestUserId);

    comment.updateStatus(CommentStatus.DELETED);

    log.info("[COMMENT_LOGICAL_DELETE] Logical Deleted Comment: id={}", comment.getId());

    // 댓글 수 감소
    reviewRepository.decrementCommentCount(comment.getReviewId());
  }

  // 댓글 물리 삭제
  @Transactional
  public void hardDelete(UUID commentId, UUID requestUserId) {
    // 댓글 조회
    Comment comment = getCommentOrThrow(commentId);
    // 댓글 상태 검증
    validCommentStatus(comment);

    // 요청자 상태 검증
    validUserStatus(getUserOrThrow(requestUserId));

    // 요청자와 댓글의 작성자 ID 비교
    validAuthor(comment, requestUserId);

    commentRepository.deleteById(commentId);

    log.info("[COMMENT_PHYSICAL_DELETE] Physical Deleted Comment: id={}", comment.getId());

    reviewRepository.decrementCommentCount(comment.getReviewId());
  }

  // 리뷰 상태 검증
  private void validReviewStatus(Review review) {
    if (review.getStatus().equals(ReviewStatus.DELETED)) {
      throw new ReviewNotFoundException(review.getId());
    }
  }

  // 유저 상태 검증
  private void validUserStatus(User user) {
    if (user.getStatus().equals(UserStatus.DELETED)) {
      throw new UserNotFoundException(user.getId());
    }
  }

  // 댓글 상태 검증
  private void validCommentStatus(Comment comment) {
    if (comment.getStatus().equals(CommentStatus.DELETED)) {
      throw new CommentNotFoundException(comment.getId());
    }
  }

  // 리뷰 조회 후 반환
  private Review getReviewOrThrow(UUID reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));
  }

  // 유저 조회 후 반환
  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }

  // 댓글 조회 후 반환
  private Comment getCommentOrThrow(UUID commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));
  }

  // 댓글 작성자와 요청자 ID 비교
  private void validAuthor(Comment comment, UUID userId) {
    if (!comment.getUserId().equals(userId)) {
      throw new CommentAuthorException();
    }
  }
}