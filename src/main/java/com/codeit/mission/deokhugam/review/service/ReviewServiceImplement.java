package com.codeit.mission.deokhugam.review.service;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.book.exception.BookNotFoundException;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.notification.event.ReviewLikedEvent;
import com.codeit.mission.deokhugam.notification.repository.NotificationRepository;
import com.codeit.mission.deokhugam.review.dto.request.ReviewCreateRequest;
import com.codeit.mission.deokhugam.review.dto.request.ReviewSearchConditionDto;
import com.codeit.mission.deokhugam.review.dto.request.ReviewUpdateRequest;
import com.codeit.mission.deokhugam.review.dto.response.CursorPageResponseReviewDto;
import com.codeit.mission.deokhugam.review.dto.response.ReviewDto;
import com.codeit.mission.deokhugam.review.dto.response.ReviewLikeDto;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.entity.ReviewLike;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.exception.DuplicateReviewException;
import com.codeit.mission.deokhugam.review.exception.DuplicateReviewLikeRequestException;
import com.codeit.mission.deokhugam.review.exception.ReviewAuthorMismatchException;
import com.codeit.mission.deokhugam.review.exception.ReviewNotFoundException;
import com.codeit.mission.deokhugam.review.mapper.ReviewLikeMapper;
import com.codeit.mission.deokhugam.review.mapper.ReviewMapper;
import com.codeit.mission.deokhugam.review.repository.ReviewLikeRepository;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.entity.UserStatus;
import com.codeit.mission.deokhugam.user.exception.UserNotFoundException;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/*
    리뷰 서비스
    ---------
    리뷰 생성, 수정, 삭제 (논리 / 물리)
    리뷰 상세 조회, 정렬 및 페이지네이션이 적용된 목록 조회
    좋아요 추가 및 취소
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImplement implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final NotificationRepository notificationRepository;

  private final ReviewMapper reviewMapper;
  private final ReviewLikeMapper reviewLikeMapper;

  private final ApplicationEventPublisher eventPublisher;

  // 리뷰 상세 조회
  @Override
  public ReviewDto findById(UUID id, UUID requestUserId) {
    // 1. Review / User 조회 : 존재하지 않을 시, 오류 발생
    Review targetReview = getReviewEntityOrThrow(id);
    User requestUser = getUserEntityOrThrow(requestUserId);

    // 2. Review / Book / User 논리 삭제 여부 검증: 이미 논리적으로 삭제된 경우, 오류 발생
    validateUserActive(requestUser);
    validateBookActive(targetReview.getBook());
    validateReviewActive(targetReview);

    // 3. 특정 리뷰에 대한 요청자의 좋아요 여부 확인
    boolean isLiked = isReviewLiked(targetReview.getId(), requestUser.getId());

    // 4. 응답 DTO 변환 및 반환
    return reviewMapper.toDto(targetReview, isLiked);
  }

  // 정렬 및 페이지네이션이 적용된 리뷰 목록 조회
  @Override
  public CursorPageResponseReviewDto<ReviewDto> findAllByKeyword(UUID requestUserId,
      ReviewSearchConditionDto condition) {
    // 1. 필터링 + 정렬 + 커서 기반 페이지네이션이 적용된 리뷰 좋아요 리스트
    List<Review> reviews = reviewRepository.searchReviews(condition);

    // 2. 다음 페이지 유무 확인 | 11개를 가져왔다면 다음 페이지가 존재하는 것
    boolean hasNext = reviews.size() > condition.limit();
    List<Review> pagedReviews = hasNext
        ? reviews.subList(0, condition.limit())       // 있다면, 기본 페이지 크기 개수(10개)만큼 자르기
        : reviews;                                    // 없다면, 그대로 전달

    // 3. 이전 페이지의 마지막 요소의 생성 시간 (다음 요청의 after) 및 다음 페이지 시작점 (다음 페이지의 cursor) 설정
    String nextCursor = null;
    Instant nextAfter = null;

    // 페이징 된 리뷰 목록이 있고 다음 페이지가 존재할 때만 기록
    if (!pagedReviews.isEmpty() && hasNext) {
      Review lastItem = pagedReviews.get(pagedReviews.size() - 1);

      nextAfter = lastItem.getCreatedAt();
      nextCursor = generateNextCursor(lastItem, condition);
    }

    // 4. 페이징 된 리뷰 ID 목록 (최대 10개)
    List<UUID> reviewIds = pagedReviews.stream()
        .map(Review::getId)
        .toList();

    // 5. 목록 조회 요청자가 좋아요를 누른 리뷰 ID 목록
    List<UUID> reviewLikeIds = getReviewLikeIds(requestUserId, reviewIds);

    // 6. Review -> ReviewDto 변환
    List<ReviewDto> content = reviewMapper.toDtoList(pagedReviews, reviewLikeIds);

    // 7. 리뷰 좋아요 전체 개수 저장
    long totalElements = reviewRepository.countWithFilter(condition);

    // 8. 페이징 응답 DTO 생성 및 반환
    return reviewMapper.toCursorPageResponse(
        content,
        nextCursor,
        nextAfter,
        condition.limit(),
        totalElements,
        hasNext
    );
  }

  // 커서 생성 로직
  private String generateNextCursor(Review lastItem, ReviewSearchConditionDto condition) {
    // 가중치 접두사 계산
    String rankPrefix = generateRankPrefix(lastItem, condition.keyword());

    return "rating".equals(condition.orderBy())
        ? rankPrefix + lastItem.getRating() + "_" + lastItem.getId().toString()
        : rankPrefix + lastItem.getId().toString();
  }

  // 가중치 (rank) 접두사 계산 로직
  private String generateRankPrefix(Review lastItem, String keyword) {
    // 1. 키워드가 존재할 경우, 가중치 (rank) 설정
    if (!StringUtils.hasText(keyword)) {
      return "";
    }

    // 책 제목, 사용자 닉네임, 키워드 중 하나라도 정확하게 일치한다면 완전 일치 (가중치 1) 부여
    boolean isExact = lastItem.getBook().getTitle().equalsIgnoreCase(keyword) ||
        (lastItem.getUser() != null && lastItem.getUser().getNickname().equalsIgnoreCase(keyword))
        ||
        lastItem.getContent().equalsIgnoreCase(keyword);

    return (isExact ? "1" : "2") + "_";
  }

  // 리뷰 등록
  @Override
  @Transactional
  public ReviewDto create(ReviewCreateRequest reviewCreateRequest) {
    // 1. 중복 검사
    validateDuplicateReview(reviewCreateRequest.bookId(), reviewCreateRequest.userId());

    // 2. 같은 사용자로부터 두 번 이상 생성 요청이 들어온 경우, 동시성 문제 발생 가능
    try {
      // 3. Book / User 엔티티 조회
      Book book = getBookEntityOrThrow(reviewCreateRequest.bookId());
      User user = getUserEntityOrThrow(reviewCreateRequest.userId());

      // 4. Book / User 논리 삭제 여부 검증
      validateBookActive(book);
      validateUserActive(user);

      // 5. 리뷰 생성
      Review newReview = reviewMapper.toEntity(reviewCreateRequest, book, user);

      // 6. 리뷰 저장 및 즉시 반영하여, try-catch 블록 내에서 제약 조건 위반 예외 포착
      reviewRepository.saveAndFlush(newReview);

      // 7. 대상 도서에 리뷰 수 / 평점 반영
      bookRepository.incrementReviewCountAndRating(book.getId(), newReview.getRating());

      // 8. 로그 기록
      log.info("[REVIEW_CREATE] Create Review Id: {}]", newReview.getId());

      // 9. 리뷰 응답 DTO 변환 및 반환
      return reviewMapper.toDto(newReview, false);            // 갓 생성한 리뷰는 좋아요 0

      // 만약 동시에 똑같은 요청이 들어와서, DB 유니크 제약 (uk_book_user_active)가 발생한다면 커스텀 중복 예외 발생
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 인한 중복 데이터 삽입 시 발생하는 특정 제약 조건 위반인지 확인
      if (!isDuplicateReviewConstraintViolation(e)) {
        // 중복 리뷰가 아닌 다른 무결성 제약 위반 에러
        throw e;
      }
      throw new DuplicateReviewException(reviewCreateRequest.bookId(),
          reviewCreateRequest.userId());
    }
  }

  // 리뷰 수정
  @Override
  @Transactional
  public ReviewDto update(UUID id, UUID requestUserId, ReviewUpdateRequest reviewUpdateRequest) {
    // 1. Review / User / book 조회: 존재하지 않을 시, 오류 발생
    Review targetReview = getReviewEntityOrThrow(id);
    User requestUser = getUserEntityOrThrow(requestUserId);
    Book targetBook = getBookEntityOrThrow(targetReview.getBook().getId());

    // 2. Review / Book / User 논리 삭제 여부 검증: 이미 논리적으로 삭제된 경우, 오류 발생
    validateReviewActive(targetReview);
    validateBookActive(targetReview.getBook());
    validateUserActive(requestUser);

    // 3. 권한 확인: 본인이 작성한 리뷰에 대해서만 수정 가능
    validateOwner(targetReview, requestUser);

    // 4. 리뷰 수정 및 도서에 반영
    bookRepository.decrementReviewCountAndRating(targetBook.getId(), targetReview.getRating());
    targetReview.updateContentAndRating(reviewUpdateRequest.content(),
        reviewUpdateRequest.rating());
    reviewRepository.save(targetReview);

    // 5. 대상 도서에 리뷰 수 / 평점 반영
    bookRepository.incrementReviewCountAndRating(targetBook.getId(), targetReview.getRating());

    // 6. 로그 작성
    log.info("[REVIEW_UPDATE] Update Review Id: {}", targetReview.getId());

    // 7. 특정 리뷰에 대한 작성자의 좋아요 여부 확인
    boolean isLiked = isReviewLiked(targetReview.getId(), requestUser.getId());

    // 8. 리뷰 응답 DTO 반환 및 변환
    return reviewMapper.toDto(targetReview, isLiked);
  }

  // 리뷰 논리 삭제
  @Override
  @Transactional
  public void delete(UUID id, UUID requestUserId) {
    // 1. Review / User/ Book 조회: 존재하지 않을 시, 오류 발생
    Review targetReview = getReviewEntityOrThrow(id);
    User requestUser = getUserEntityOrThrow(requestUserId);
    Book targetBook = getBookEntityOrThrow(targetReview.getBook().getId());

    // 2. Review / User 논리 삭제 여부 검증: 이미 논리적으로 삭제된 경우, 오류 발생
    validateReviewActive(targetReview);
    validateUserActive(requestUser);

    // 3. 권한 확인: 본인이 작성한 리뷰에 대해서만 삭제 가능
    validateOwner(targetReview, requestUser);

    // 4. 리뷰 논리 삭제
    targetReview.delete();

    // 5. 대상 도서에 리뷰 수 / 평점 반영
    bookRepository.decrementReviewCountAndRating(targetBook.getId(), targetReview.getRating());

    // 6. 로그 기록
    log.info("[REVIEW_LOGICAL_DELETE] Logical Delete Review Id: {}", targetReview.getId());
  }

  // 리뷰 물리 삭제
  @Override
  @Transactional
  public void hardDelete(UUID id, UUID requestUserId) {
    // 1. Review / User 조회: 존재하지 않을 시, 오류 발생
    Review targetReview = getReviewEntityOrThrow(id);
    User requestUser = getUserEntityOrThrow(requestUserId);
    Book targetBook = getBookEntityOrThrow(targetReview.getBook().getId());

    // 2. User 논리 삭제 여부 검증: 이미 논리적으로 삭제된 경우, 오류 발생
    validateUserActive(requestUser);

    // 3. 권한 확인: 본인이 작성한 리뷰에 대해서만 삭제 가능
    validateOwner(targetReview, requestUser);

    // 4. 연관 데이터 삭제 (댓글, 좋아요, 알림)
    List<UUID> reviewIds = List.of(targetReview.getId());
    commentRepository.deleteByReviewIdIn(reviewIds);
    reviewLikeRepository.deleteByReviewIdIn(reviewIds);
    notificationRepository.deleteByReviewIdIn(reviewIds);

    // 5. 리뷰 물리 삭제
    reviewRepository.delete(targetReview);

    // 6. 대상 도서에 리뷰 수 / 평점 반영
    if (targetReview.isActive()) {                          // 중복 집계 방지를 위해, 활성화 된 리뷰만 반영
      bookRepository.decrementReviewCountAndRating(targetBook.getId(), targetReview.getRating());
    }

    // 7. 로그 기록
    log.info("[REVIEW_Hard_DELETE] Hard Delete Review Id: {}", targetReview.getId());
  }

  // 리뷰 좋아요 추가 및 취소
  @Override
  @Transactional
  public ReviewLikeDto toggleLike(UUID id, UUID requestUserId) {
    // 1. Review / User 조회: 존재하지 않을 시, 오류 발생
    Review targetReview = getReviewEntityWithPessimisticLockOrThrow(id);
    User requestUser = getUserEntityOrThrow(requestUserId);

    // 2. Review / Book / User 논리 삭제 여부 검증: 이미 논리적으로 삭제된 경우, 오류 발생
    validateReviewActive(targetReview);
    validateBookActive(targetReview.getBook());
    validateUserActive(requestUser);

    // 3. 좋아요 추가 및 취소 (동시성 처리 포함)
    boolean isLiked = executeToggleWithConcurrencyHandle(targetReview, requestUser);

    // 3-1. 좋아요를 추가한 경우 이벤트 발생
    if (isLiked && targetReview.getUser() != null) {
      eventPublisher.publishEvent(
          new ReviewLikedEvent(
              requestUser.getId(),
              targetReview.getUser().getId(),
              targetReview.getId()
          )
      );
    }

    // 4. 응답 DTO 생성 및 반환
    return reviewLikeMapper.toDto(targetReview, requestUser, isLiked);
  }

  // 좋아요 추가 및 생성 동시성 문제 해결을 위한 메서드
  private boolean executeToggleWithConcurrencyHandle(Review review, User requestUser) {
    try {
      // 1. 특정 리뷰에 대한 요청자의 좋아요 여부 확인
      boolean isLiked = isReviewLiked(review.getId(), requestUser.getId());

      // 특정 리뷰에 대한 사용자의 좋아요가 존재하지 않을 경우, 좋아요 추가
      if (!isLiked) {
        processAddLike(review, requestUser);
        return true;
      } else {
        // 특정 리뷰에 대한 사용자의 좋아요가 존재할 경우, 좋아요 취소
        processRemoveLike(review, requestUser);
        return false;
      }
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 인한 중복 데이터 삽입 시 발생하는 특정 제약 조건 위반인지 확인
      if (!isDuplicateReviewLikeConstraintViolation(e)) {
        // 리뷰 좋아요 요청이 아닌 예외
        throw e;
      }
      throw new DuplicateReviewLikeRequestException(review.getId(), requestUser.getId());
    }
  }

  // 좋아요 수 증가
  private void processAddLike(Review review, User user) {
    // 1. 좋아요 생성
    ReviewLike createdReviewLike = reviewLikeMapper.toEntity(review, user);
    reviewLikeRepository.saveAndFlush(createdReviewLike);

    // 2. 특정 리뷰의 좋아요 수 증가
    reviewRepository.incrementLikeCount(review.getId());

    // 3. 로그 기록
    log.info("[ADD_REVIEW_LIKE] Add Like Id: {}", createdReviewLike.getId());
  }

  // 좋아요 수 감소
  private void processRemoveLike(Review review, User user) {
    // 1. 삭제할 리뷰 좋아요 조회: 해당 리뷰가 존재할 때만 삭제 로직 수행
    reviewLikeRepository.findByReviewIdAndUserId(review.getId(), user.getId())
        .ifPresent(targetReviewLike -> {
          // 2. 리뷰 좋아요 삭제
          reviewLikeRepository.delete(targetReviewLike);
          reviewLikeRepository.flush();

          // 3. 좋아요 수 감소
          reviewRepository.decrementLikeCount(review.getId());

          // 4. 로그 기록
          log.info("[REMOVED_REVIEW_LIKE] Remove Like Id: {}", targetReviewLike.getId());
        });
  }

  // Review 엔티티 반환
  private Review getReviewEntityOrThrow(UUID id) {
    return reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewNotFoundException(id));
  }

  // Review 엔티티 반환 (비관적 락 적용)
  private Review getReviewEntityWithPessimisticLockOrThrow(UUID id) {
    return reviewRepository.findByIdWithPessimisticLock(id)
        .orElseThrow(() -> new ReviewNotFoundException(id));
  }

  // Book 엔티티 반환
  private Book getBookEntityOrThrow(UUID bookId) {
    return bookRepository.findById(bookId)
        .orElseThrow(BookNotFoundException::new);
  }

  // User 엔티티 반환
  private User getUserEntityOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  // ReviewLike ID 목록 조회
  private List<UUID> getReviewLikeIds(UUID userId, List<UUID> reviewIds) {
    if (userId == null || reviewIds.isEmpty()) {
      return Collections.emptyList();
    }

    return reviewLikeRepository.findReviewIdsByUserIdAndReviewIdIn(userId, reviewIds);
  }

  // 유효성 검증 (중복 검사): 사용자가 이미 특정 도서에 리뷰를 남긴 경우, 예외 발생
  private void validateDuplicateReview(UUID bookId, UUID userId) {
    if (reviewRepository.existsByBookIdAndUserIdAndStatus(bookId, userId, ReviewStatus.ACTIVE)) {
      throw new DuplicateReviewException(bookId, userId);
    }
  }

  // 유효성 검증 (권한 확인): 요청자와 리뷰 작성자가 다를 경우, 예외 발생
  private void validateOwner(Review targetReview, User requestUser) {
    boolean isOwner = targetReview.getUser().getId().equals(requestUser.getId());

    if (!isOwner) {
      throw new ReviewAuthorMismatchException(targetReview.getUser().getId(), requestUser.getId());
    }
  }

  // 유효성 검증 (도서 논리 삭제 여부 확인): 이미 논리적으로 삭제된 도서일 경우, 예외 발생
  private void validateBookActive(Book targetBook) {
    if (targetBook.getBookStatus() == BookStatus.DELETED) {
      throw new BookNotFoundException();
    }
  }

  // 유효성 검증 (사용자 논리 삭제 여부 확인): 이미 논리적으로 삭제된 사용자일 경우, 예외 발생
  private void validateUserActive(User targetUser) {
    if (targetUser.getStatus() == UserStatus.DELETED) {
      throw new UserNotFoundException(targetUser.getId());
    }
  }

  // 유효성 검증 (리뷰 논리 삭제 여부 확인): 이미 논리적으로 삭제된 리뷰일 경우, 예외 발생
  private void validateReviewActive(Review targetReview) {
    if (targetReview.getStatus() == ReviewStatus.DELETED) {
      throw new ReviewNotFoundException(targetReview.getId());
    }
  }

  // 특정 사용자의 리뷰 좋아요 여부 확인
  private boolean isReviewLiked(UUID reviewId, UUID userId) {
    return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
  }

  // 유니크 제약 조건 (uk_book_user) 위반 확인: 발생한 예외가 중복 리뷰 예외에 해당하는지 확인
  private boolean isDuplicateReviewConstraintViolation(DataIntegrityViolationException e) {
    Throwable cause = e.getMostSpecificCause();

    return cause != null && cause.getMessage() != null && cause.getMessage()
        .contains("uk_book_user_active");
  }

  // 유니크 제약 조건 (uk_review_user_like) 위반 확인: 발생한 예외가 중복 리뷰 좋아요 요청 예외에 해당하는지 확인
  private boolean isDuplicateReviewLikeConstraintViolation(DataIntegrityViolationException e) {
    Throwable cause = e.getMostSpecificCause();

    return cause != null && cause.getMessage() != null && cause.getMessage()
        .contains("uk_review_user_like");
  }
}
