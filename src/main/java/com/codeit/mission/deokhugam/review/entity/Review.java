package com.codeit.mission.deokhugam.review.entity;

import com.codeit.mission.deokhugam.base.BaseEntity;
import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.review.exception.InvalidReviewRatingRangeException;
import com.codeit.mission.deokhugam.review.exception.ReviewContentBlankException;
import com.codeit.mission.deokhugam.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/*
    Review
    -------
    사용자가 도서 별로 하나씩 작성한 리뷰 정보
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "reviews",                                           // 데이터베이스 테이블 이름
    uniqueConstraints = {                                       // 중복 방지를 위한 제약 조건 설정
        @UniqueConstraint(
            name = "uk_book_user_active",
            columnNames = {"book_id", "user_id", "deleted_at"}
        )
    }
)
public class Review extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;                                           // 리뷰 대상 도서

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @NotFound(action = NotFoundAction.IGNORE)                    // 탈퇴한 사용자 null 처리
  private User user;                                           // 리뷰 작성자

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;                                      // 리뷰 내용

  @Column(nullable = false)
  @Min(1)
  @Max(5)
  private int rating;                                          // 리뷰 평점

  @Column(nullable = false)
  private int likeCount = 0;                                   // 리뷰의 좋아요 수 (기본값: 0)

  @Column(nullable = false)
  private int commentCount = 0;                                // 리뷰의 댓글 수 (기본값: 0)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReviewStatus status = ReviewStatus.ACTIVE;           // 리뷰 상태 (기본값: 활성)

  private Instant deletedAt;                                   // 리뷰 논리 삭제 시점

  // 생성자: 빌더 패턴을 통해 객체 생성 시, 유효성 검증 강제 수행
  @Builder
  public Review(Book book, User user, String content, int rating) {
    validateContent(content);                           // 내용 검증
    validateRating(rating);                             // 평점 검증

    this.book = book;
    this.user = user;
    this.content = content;
    this.rating = rating;
  }

  // 리뷰 수정
  public void updateContentAndRating(String content, int rating) {
    validateContent(content);
    validateRating(rating);

    this.content = content;
    this.rating = rating;
  }

  // 유효성 검증 (평점): 평점 범위(0~5)를 벗어날 경우, 예외 발생
  private void validateRating(int rating) {
    if (rating < 1 || rating > 5) {
      throw new InvalidReviewRatingRangeException(rating);
    }
  }

  // 유효성 검증 (내용): 내용이 비어있을 경우 예외 발생
  private void validateContent(String content) {
    if (content == null || content.isBlank()) {
      throw new ReviewContentBlankException(content);
    }
  }

  // 리뷰 논리 삭제: 리뷰 상태 변경 및 삭제 시간 기록
  public void delete() {
    // 최초 삭제 시점 보존을 위한 중복 삭제 방지
    if (this.status == ReviewStatus.DELETED) {
      return;
    }

    this.status = ReviewStatus.DELETED;
    this.deletedAt = Instant.now();
  }

  // 해당 리뷰의 활성 여부 검증
  public boolean isActive() {
    return this.status == ReviewStatus.ACTIVE;
  }
}
