package com.codeit.mission.deokhugam.review.repository;

import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.request.BookReviewAvgRating;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.request.BookReviewCount;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.request.ReviewLikeCount;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.UserReviewAggregate;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.repository.custom.ReviewRepositoryCustom;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

  // 단건 조회 시, 탈퇴한 사용자로 인한 N + 1 방지
  @EntityGraph(attributePaths = {"user"})
  Optional<Review> findById(UUID id);

  // 중복 검사: 특정 도서에 대해 활성화 된 사용자 리뷰 존재 유무
  boolean existsByBookIdAndUserIdAndStatus(UUID bookId, UUID userId, ReviewStatus status);

  // 비관적 락이 적용된 리뷰 조회
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @EntityGraph(attributePaths = {"user"})
  @Query("SELECT review FROM Review review WHERE review.id = :id")
  Optional<Review> findByIdWithPessimisticLock(@Param("id") UUID id);

  // 좋아요 수 증가
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("UPDATE Review review SET review.likeCount = review.likeCount + 1 WHERE review.id = :reviewId")
  void incrementLikeCount(@Param("reviewId") UUID reviewId);

  // 좋아요 수 감소
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("UPDATE Review review SET review.likeCount = review.likeCount - 1 WHERE review.id = :reviewId AND review.likeCount > 0")
  void decrementLikeCount(@Param("reviewId") UUID reviewId);

  // 댓글 수 증가
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("UPDATE Review  review SET review.commentCount = review.commentCount + 1 WHERE review.id = :reviewId")
  void incrementCommentCount(@Param("reviewId") UUID reviewId);

  // 댓글 수 감소
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("UPDATE Review  review SET review.commentCount = review.commentCount - 1 WHERE review.id = :reviewId AND review.commentCount > 0")
  void decrementCommentCount(@Param("reviewId") UUID reviewId);

  // 유저 Id별 리뷰의 점수 총계를 리턴하는 메서드
  @Query(
      """
          select new com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.UserReviewAggregate(
              r.user.id,
              coalesce(sum(r.rating), 0.0)
          )
          from Review r
          where r.createdAt >= :periodStart
            and r.createdAt < :periodEnd
            and r.status = :status
          group by r.user.id
          """)
  List<UserReviewAggregate> findUserReviewAggregates(
      @Param("periodStart") Instant periodStart,
      @Param("periodEnd") Instant periodEnd,
      @Param("status") ReviewStatus status);

  // 사용자가 누른 '좋아요' 기록들을 일괄 삭제 (외래 키 제약 조건 해결용)
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM review_likes WHERE user_id IN :userIds", nativeQuery = true)
  void deleteLikesByUserIds(@Param("userIds") List<UUID> userIds);

  // 삭제 대상 유저들이 작성한 리뷰에 달린 모든 좋아요 삭제 (서브쿼리 활용)
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM review_likes WHERE review_id IN (SELECT id FROM reviews WHERE user_id IN :userIds)", nativeQuery = true)
  void deleteLikesByReviewUserIds(@Param("userIds") List<UUID> userIds);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM reviews WHERE user_id IN :userIds", nativeQuery = true)
  void deleteByUserIds(@Param("userIds") List<UUID> userIds);

  // 기간 내 리뷰 당 받은 좋아요 수를 뽑는 쿼리
  @Query("""
      select new com.codeit.mission.deokhugam.dashboard.popularreviews.dto.request.ReviewLikeCount(
          rl.review.id,
          count(rl)
      )
      from ReviewLike rl
      where rl.likedAt >= :periodStart
        and rl.likedAt < :periodEnd
        and rl.review.status = :status
      group by rl.review.id
      """)
  List<ReviewLikeCount> countReviewLikes(
      @Param("periodStart") Instant periodStart,
      @Param("periodEnd") Instant periodEnd,
      @Param("status") ReviewStatus status);

  // 기간 내 책 별 리뷰 개수를 뽑는 쿼리
  @Query("""
          select new com.codeit.mission.deokhugam.dashboard.popularbooks.dto.request.BookReviewCount(
            b.id,
            count(r)
          ) from Book b
          join Review r on r.book.id = b.id
          where r.createdAt >= :periodStart 
          and r.createdAt < :periodEnd
          and r.status = :status
          group by b.id
      """)
  List<BookReviewCount> countReviewsPerBook(
      @Param("periodStart") Instant periodStart,
      @Param("periodEnd") Instant periodEnd,
      @Param("status") ReviewStatus status);

  // 기간 내 책 리뷰의 평균을 구하는 쿼리
  @Query("""
          select new com.codeit.mission.deokhugam.dashboard.popularbooks.dto.request.BookReviewAvgRating(
            r.book.id,
            avg(r.rating * 1.0)
          )
          from Review r
          where r.createdAt >= :periodStart
          and r.createdAt < :periodEnd
          and r.status = :status
          group by r.book.id
      """)
  List<BookReviewAvgRating> avgRatingsPerBook(
      @Param("periodStart") Instant periodStart,
      @Param("periodEnd") Instant periodEnd,
      @Param("status") ReviewStatus status
  );

  List<Review> findByIdIn(Collection<UUID> ids);
}
