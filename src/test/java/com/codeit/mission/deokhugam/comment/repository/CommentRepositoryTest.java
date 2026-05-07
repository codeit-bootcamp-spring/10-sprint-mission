package com.codeit.mission.deokhugam.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import com.codeit.mission.deokhugam.comment.dto.request.CommentFindAllRequest;
import com.codeit.mission.deokhugam.comment.entity.Comment;
import com.codeit.mission.deokhugam.comment.entity.CommentStatus;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test") // 테스트 프로파일로 실행할 때에만 적용
@Import(CommentRepositoryTest.QueryDslTestConfig.class)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;

    @TestConfiguration
    static class QueryDslTestConfig {

        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Test
    @DisplayName("리뷰 ID와 상태로 댓글 수를 조회한다")
    void countByReviewIdAndStatus_success() {
        // given
        UUID reviewId = createReview();
        UUID otherReviewId = createReview();

        commentRepository.save(createComment(reviewId, createUserId(), "댓글1", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, createUserId(), "댓글2", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, createUserId(), "삭제 댓글", CommentStatus.DELETED));
        commentRepository.save(createComment(otherReviewId, createUserId(), "다른 리뷰 댓글", CommentStatus.ACTIVE));

        // when
        long count = commentRepository.countByReviewIdAndStatus(reviewId, CommentStatus.ACTIVE);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("기간별 사용자 댓글 수를 집계한다")
    void findUserCommentCounts_success() {
        // given
        UUID userId1 = createUserId();
        UUID userId2 = createUserId();
        UUID reviewId = createReview();

        commentRepository.save(createComment(reviewId, userId1, "댓글1", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, userId1, "댓글2", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, userId2, "댓글3", CommentStatus.ACTIVE));

        em.flush();
        em.clear();

        Instant periodStart = Instant.now().minusSeconds(60);
        Instant periodEnd = Instant.now().plusSeconds(60);

        // when
        var results = commentRepository.findUserCommentCounts(periodStart, periodEnd);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .anySatisfy(result -> {
                    assertThat(result.userId()).isEqualTo(userId1);
                    assertThat(result.commentCount()).isEqualTo(2);
                })
                .anySatisfy(result -> {
                    assertThat(result.userId()).isEqualTo(userId2);
                    assertThat(result.commentCount()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("기간별 리뷰 댓글 수를 집계한다")
    void findReviewCommentCounts_success() {
        // given
        UUID reviewId1 = createReview();
        UUID reviewId2 = createReview();

        commentRepository.save(createComment(reviewId1, createUserId(), "댓글1", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId1, createUserId(), "댓글2", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId2, createUserId(), "댓글3", CommentStatus.ACTIVE));

        em.flush();
        em.clear();

        Instant periodStart = Instant.now().minusSeconds(60);
        Instant periodEnd = Instant.now().plusSeconds(60);

        // when
        var results = commentRepository.findReviewCommentCounts(periodStart, periodEnd);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .anySatisfy(result -> {
                    assertThat(result.reviewId()).isEqualTo(reviewId1);
                    assertThat(result.commentCount()).isEqualTo(2);
                })
                .anySatisfy(result -> {
                    assertThat(result.reviewId()).isEqualTo(reviewId2);
                    assertThat(result.commentCount()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("커서 조회는 ACTIVE 댓글만 조회한다")
    void findAllByCursor_onlyActiveComments() {
        // given
        UUID reviewId = createReview();

        Comment activeComment = commentRepository.save(
                createComment(reviewId, createUserId(), "정상 댓글", CommentStatus.ACTIVE)
        );

        commentRepository.save(
                createComment(reviewId, createUserId(), "삭제 댓글", CommentStatus.DELETED)
        );

        em.flush();
        em.clear();

        CommentFindAllRequest request = new CommentFindAllRequest(
                reviewId,
                "ASC",
                null,
                null,
                10
        );

        // when
        List<Comment> results = commentRepository.findAllByCursor(request);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(activeComment.getId());
    }

    @Test
    @DisplayName("커서 조회는 limit + 1개를 조회한다")
    void findAllByCursor_fetchLimitPlusOne() {
        // given
        UUID reviewId = createReview();

        commentRepository.save(createComment(reviewId, createUserId(), "댓글1", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, createUserId(), "댓글2", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, createUserId(), "댓글3", CommentStatus.ACTIVE));

        em.flush();
        em.clear();

        CommentFindAllRequest request = new CommentFindAllRequest(
                reviewId,
                "ASC",
                null,
                null,
                2
        );

        // when
        List<Comment> results = commentRepository.findAllByCursor(request);

        // then
        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("ASC 커서 조회는 동일 createdAt에서 cursor id보다 큰 댓글만 조회한다")
    void findAllByCursor_ascSameCreatedAtUsesIdTieBreaker() {
        // given
        UUID reviewId = createReview();
        Instant sameCreatedAt = Instant.parse("2026-01-01T00:00:00Z");

        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글1", CommentStatus.ACTIVE, sameCreatedAt)
        );
        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글2", CommentStatus.ACTIVE, sameCreatedAt)
        );
        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글3", CommentStatus.ACTIVE, sameCreatedAt)
        );

        em.flush();
        em.clear();

        CommentFindAllRequest firstPageRequest = new CommentFindAllRequest(
                reviewId,
                "ASC",
                null,
                null,
                10
        );

        List<Comment> firstPage = commentRepository.findAllByCursor(firstPageRequest);

        assertThat(firstPage).hasSize(3);

        Comment cursorComment = firstPage.get(1);
        Comment expectedComment = firstPage.get(2);

        CommentFindAllRequest cursorRequest = new CommentFindAllRequest(
                reviewId,
                "ASC",
                cursorComment.getId().toString(),
                cursorComment.getCreatedAt(),
                10
        );

        // when
        List<Comment> results = commentRepository.findAllByCursor(cursorRequest);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(expectedComment.getId());
    }

    @Test
    @DisplayName("DESC 커서 조회는 동일 createdAt에서 cursor id보다 작은 댓글만 조회한다")
    void findAllByCursor_descSameCreatedAtUsesIdTieBreaker() {
        // given
        UUID reviewId = createReview();
        Instant sameCreatedAt = Instant.parse("2026-01-01T00:00:00Z");

        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글1", CommentStatus.ACTIVE, sameCreatedAt)
        );
        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글2", CommentStatus.ACTIVE, sameCreatedAt)
        );
        commentRepository.save(
                createCommentWithCreatedAt(reviewId, createUserId(), "댓글3", CommentStatus.ACTIVE, sameCreatedAt)
        );

        em.flush();
        em.clear();

        CommentFindAllRequest firstPageRequest = new CommentFindAllRequest(
                reviewId,
                "DESC",
                null,
                null,
                10
        );

        List<Comment> firstPage = commentRepository.findAllByCursor(firstPageRequest);

        assertThat(firstPage).hasSize(3);

        Comment cursorComment = firstPage.get(1);
        Comment expectedComment = firstPage.get(2);

        CommentFindAllRequest cursorRequest = new CommentFindAllRequest(
                reviewId,
                "DESC",
                cursorComment.getId().toString(),
                cursorComment.getCreatedAt(),
                10
        );

        // when
        List<Comment> results = commentRepository.findAllByCursor(cursorRequest);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(expectedComment.getId());
    }

    @Test
    @DisplayName("사용자 ID 목록으로 댓글을 일괄 삭제한다")
    void deleteByUserIds_success() {
        // given
        UUID targetUserId = createUserId();
        UUID otherUserId = createUserId();
        UUID reviewId = createReview();

        commentRepository.save(createComment(reviewId, targetUserId, "삭제 대상", CommentStatus.ACTIVE));
        commentRepository.save(createComment(reviewId, otherUserId, "유지 대상", CommentStatus.ACTIVE));

        em.flush();
        em.clear();

        // when
        commentRepository.deleteByUserIds(List.of(targetUserId));

        em.flush();
        em.clear();

        // then
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getUserId()).isEqualTo(otherUserId);
    }

    @Test
    @DisplayName("리뷰 ID 목록으로 댓글을 일괄 삭제한다")
    void deleteByReviewIdIn_success() {
        // given
        UUID targetReviewId = createReview();
        UUID otherReviewId = createReview();

        commentRepository.save(createComment(targetReviewId, createUserId(), "삭제 대상", CommentStatus.ACTIVE));
        commentRepository.save(createComment(otherReviewId, createUserId(), "유지 대상", CommentStatus.ACTIVE));

        em.flush();
        em.clear();

        // when
        commentRepository.deleteByReviewIdIn(List.of(targetReviewId));

        em.flush();
        em.clear();

        // then
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getReviewId()).isEqualTo(otherReviewId);
    }

    private Comment createComment(
            UUID reviewId,
            UUID userId,
            String content,
            CommentStatus status
    ) {
        Comment comment = Comment.builder()
                .reviewId(reviewId)
                .userId(userId)
                .content(content)
                .status(status)
                .build();

        Instant now = Instant.now();

        ReflectionTestUtils.setField(comment, "createdAt", now);
        ReflectionTestUtils.setField(comment, "updatedAt", now);

        return comment;
    }

    private UUID createReview() {
        Instant now = Instant.now();

        User user = User.builder()
                .email("test-" + UUID.randomUUID() + "@test.com")
                .nickname("테스트유저")
                .password("password")
                .build();

        ReflectionTestUtils.setField(user, "createdAt", now);
        ReflectionTestUtils.setField(user, "updatedAt", now);

        User savedUser = userRepository.save(user);

        Book book = Book.builder()
                .title("테스트 책")
                .author("테스트 저자")
                .description("테스트 설명")
                .publisher("테스트 출판사")
                .publishedDate(LocalDate.of(2026, 1, 1))
                .isbn("isbn-" + UUID.randomUUID().toString().substring(0, 8))
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .reviewCount(0)
                .rating(0.0)
                .build();

        ReflectionTestUtils.setField(book, "createdAt", now);
        ReflectionTestUtils.setField(book, "updatedAt", now);

        Book savedBook = bookRepository.save(book);

        Review review = Review.builder()
                .book(savedBook)
                .user(savedUser)
                .content("테스트 리뷰")
                .rating(5)
                .build();

        ReflectionTestUtils.setField(review, "createdAt", now);
        ReflectionTestUtils.setField(review, "updatedAt", now);

        Review savedReview = reviewRepository.save(review);

        return savedReview.getId();
    }

    private UUID createUserId() {
        Instant now = Instant.now();

        User user = User.builder()
                .email("comment-user-" + UUID.randomUUID() + "@test.com")
                .nickname("댓글유저")
                .password("password")
                .build();

        ReflectionTestUtils.setField(user, "createdAt", now);
        ReflectionTestUtils.setField(user, "updatedAt", now);

        return userRepository.save(user).getId();
    }

    private Comment createCommentWithCreatedAt(
            UUID reviewId,
            UUID userId,
            String content,
            CommentStatus status,
            Instant createdAt
    ) {
        Comment comment = Comment.builder()
                .reviewId(reviewId)
                .userId(userId)
                .content(content)
                .status(status)
                .build();

        ReflectionTestUtils.setField(comment, "createdAt", createdAt);
        ReflectionTestUtils.setField(comment, "updatedAt", createdAt);

        return comment;
    }
}
