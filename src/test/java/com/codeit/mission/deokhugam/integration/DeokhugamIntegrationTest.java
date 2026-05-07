package com.codeit.mission.deokhugam.integration;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import com.codeit.mission.deokhugam.comment.entity.Comment;
import com.codeit.mission.deokhugam.comment.entity.CommentStatus;
import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeokhugamIntegrationTest {

    @MockitoBean
    S3Client s3Client;

    @MockitoBean
    S3Presigner s3Presigner;

    @TestConfiguration
    static class TestCacheConfig {
        @Bean("testCacheManger")
        @Primary
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(
                    "popularBooks",
                    "popularReviews"
            );
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;
    @Autowired BookRepository bookRepository;
    @Autowired ReviewRepository reviewRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CacheManager cacheManager;

    @BeforeEach
    void cleanUp() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        commentRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("1. 사용자 → 도서 → 리뷰 → 댓글 전체 흐름")
    class UserBookReviewCommentFlow {

        @Test
        @DisplayName("사용자, 도서, 리뷰, 댓글 생성 후 댓글이 저장된다")
        void createFullFlow_success() throws Exception {
            // given
            User reviewer = saveUser("reviewer@test.com", "reviewer");
            User commenter = saveUser("commenter@test.com", "commenter");
            Book book = saveBook("9788966262281");
            Review review = saveReview(reviewer, book);

            Map<String, Object> request = Map.of(
                    "reviewId", review.getId(),
                    "userId", commenter.getId(),
                    "content", "통합 테스트 댓글"
            );

            // when & then
            mockMvc.perform(post("/api/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.reviewId").value(review.getId().toString()))
                    .andExpect(jsonPath("$.userId").value(commenter.getId().toString()))
                    .andExpect(jsonPath("$.content").value("통합 테스트 댓글"));

            mockMvc.perform(get("/api/comments")
                            .param("reviewId", review.getId().toString())
                            .param("direction", "DESC")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].reviewId").value(review.getId().toString()))
                    .andExpect(jsonPath("$.content[0].userId").value(commenter.getId().toString()))
                    .andExpect(jsonPath("$.content[0].content").value("통합 테스트 댓글"));
        }
    }

    @Nested
    @DisplayName("2. 댓글 수정/삭제 권한")
    class CommentAuthorization {

        @Test
        @DisplayName("댓글 작성자는 댓글을 수정할 수 있다")
        void updateComment_success() throws Exception {
            // given
            User user = saveUser("user@test.com", "user");
            Book book = saveBook("9788966262282");
            Review review = saveReview(user, book);
            Comment comment = saveComment(review.getId(), user.getId(), "수정 전");

            Map<String, Object> request = Map.of(
                    "content", "수정 후"
            );

            // when & then
            mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                            .header("Deokhugam-Request-User-ID", user.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value("수정 후"));
        }

        @Test
        @DisplayName("댓글 작성자가 아니면 댓글을 수정할 수 없다")
        void updateComment_fail_whenNotAuthor() throws Exception {
            // given
            User author = saveUser("author@test.com", "author");
            User other = saveUser("other@test.com", "other");
            Book book = saveBook("9788966262283");
            Review review = saveReview(author, book);
            Comment comment = saveComment(review.getId(), author.getId(), "원본 댓글");

            Map<String, Object> request = Map.of(
                    "content", "수정 시도"
            );

            // when & then
            mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                            .header("Deokhugam-Request-User-ID", other.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("댓글 작성자는 댓글을 논리 삭제할 수 있다")
        void softDeleteComment_success() throws Exception {
            // given
            User user = saveUser("delete@test.com", "deleteUser");
            Book book = saveBook("9788966262284");
            Review review = saveReview(user, book);
            Comment comment = saveComment(review.getId(), user.getId(), "삭제될 댓글");

            // when & then
            mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                            .header("Deokhugam-Request-User-ID", user.getId().toString()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("3. 리뷰 중복 작성 방지")
    class ReviewDuplicate {

        @Test
        @DisplayName("같은 사용자는 같은 도서에 리뷰를 2개 작성할 수 없다")
        void createReview_fail_whenDuplicated() throws Exception {
            // given
            User user = saveUser("review@test.com", "reviewUser");
            Book book = saveBook("9788966262285");
            saveReview(user, book);

            Map<String, Object> request = Map.of(
                    "bookId", book.getId(),
                    "userId", user.getId(),
                    "content", "중복 리뷰",
                    "rating", 5
            );

            // when & then
            mockMvc.perform(post("/api/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("4. 리뷰 수정/삭제 권한")
    class ReviewAuthorization {

        @Test
        @DisplayName("리뷰 작성자는 리뷰를 수정할 수 있다")
        void updateReview_success() throws Exception {
            // given
            User user = saveUser("review-author@test.com", "reviewAuthor");
            Book book = saveBook("9788966262286");
            Review review = saveReview(user, book);

            Map<String, Object> request = Map.of(
                    "content", "수정된 리뷰",
                    "rating", 4
            );

            // when & then
            mockMvc.perform(patch("/api/reviews/{reviewId}", review.getId())
                            .header("Deokhugam-Request-User-ID", user.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value("수정된 리뷰"));
        }

        @Test
        @DisplayName("리뷰 작성자가 아니면 리뷰를 수정할 수 없다")
        void updateReview_fail_whenNotAuthor() throws Exception {
            // given
            User author = saveUser("review-author2@test.com", "author");
            User other = saveUser("review-other@test.com", "other");
            Book book = saveBook("9788966262287");
            Review review = saveReview(author, book);

            Map<String, Object> request = Map.of(
                    "content", "권한 없는 수정",
                    "rating", 1
            );

            // when & then
            mockMvc.perform(patch("/api/reviews/{reviewId}", review.getId())
                            .header("Deokhugam-Request-User-ID", other.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("5. 댓글 목록 커서 페이지네이션")
    class CommentCursorPagination {

        @Test
        @DisplayName("댓글 목록을 limit 기준으로 조회하고 다음 페이지 정보를 반환한다")
        void findComments_success_withCursor() throws Exception {
            // given
            User user = saveUser("cursor@test.com", "cursorUser");
            Book book = saveBook("9788966262288");
            Review review = saveReview(user, book);

            saveComment(review.getId(), user.getId(), "댓글1");
            saveComment(review.getId(), user.getId(), "댓글2");
            saveComment(review.getId(), user.getId(), "댓글3");

            // when & then
            mockMvc.perform(get("/api/comments")
                            .param("reviewId", review.getId().toString())
                            .param("direction", "DESC")
                            .param("limit", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.nextCursor").exists())
                    .andExpect(jsonPath("$.nextAfter").exists());
        }
    }

    @Nested
    @DisplayName("6. 리뷰 목록 정렬/페이지네이션")
    class ReviewPagination {

        @Test
        @DisplayName("리뷰 목록을 시간순으로 페이지네이션 조회한다")
        void findReviews_success_withCursor() throws Exception {
            // given
            User user = saveUser("review-page@test.com", "reviewPageUser");
            Book book1 = saveBook("9788966262289");
            Book book2 = saveBook("9788966262290");
            Book book3 = saveBook("9788966262291");

            saveReview(user, book1);
            saveReview(user, book2);
            saveReview(user, book3);

            // when & then
            mockMvc.perform(get("/api/reviews")
                            .param("requestUserId", user.getId().toString())
                            .header("Deokhugam-Request-User-ID", user.getId().toString())
                            .param("orderBy", "createdAt")
                            .param("direction", "DESC")
                            .param("limit", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.hasNext").value(true));
        }
    }

    @Nested
    @DisplayName("7. 도서 ISBN 중복/유효성")
    class BookValidation {

        @Test
        @DisplayName("같은 ISBN의 도서는 중복 등록할 수 없다")
        void createBook_fail_whenDuplicatedIsbn() throws Exception {
            // given
            String isbn = "9780134685991";
            saveBook(isbn);

            Map<String, Object> request = Map.of(
                    "title", "중복 도서",
                    "author", "테스트 저자",
                    "isbn", isbn,
                    "description", "중복 ISBN",
                    "publisher", "테스트 출판사",
                    "publishedDate", "2024-01-01"
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/books")
                            .file(bookData))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("잘못된 ISBN은 등록할 수 없다")
        void createBook_fail_whenInvalidIsbn() throws Exception {
            // given
            Map<String, Object> request = Map.of(
                    "title", "잘못된 ISBN 도서",
                    "author", "테스트 저자",
                    "isbn", "invalid-isbn",
                    "description", "잘못된 ISBN",
                    "publisher", "테스트 출판사",
                    "publishedDate", "2024-01-01"
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/books")
                            .file(bookData))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("8. 좋아요 → 알림 흐름")
    class LikeNotificationFlow {

        @Test
        @DisplayName("다른 사용자가 리뷰에 좋아요를 누르면 리뷰 작성자에게 알림이 생성된다")
        void likeReview_success_createNotification() throws Exception {
            // given
            User reviewWriter = saveUser("like-writer@test.com", "writer");
            User liker = saveUser("liker@test.com", "liker");
            Book book = saveBook("9788966262293");
            Review review = saveReview(reviewWriter, book);

            Map<String, Object> request = Map.of(
                    "userId", liker.getId()
            );

            // when
            mockMvc.perform(post("/api/reviews/{reviewId}/like", review.getId())
                            .header("Deokhugam-Request-User-ID", liker.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // then
            await()
                    .pollInterval(200, TimeUnit.MILLISECONDS)
                    .atMost(3, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        mockMvc.perform(get("/api/notifications")
                                        .param("userId", reviewWriter.getId().toString()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1));
                    });
        }
    }

    @Nested
    @DisplayName("9. 논리 삭제 데이터 조회 제외")
    class SoftDeletedDataFilter {

        @Test
        @DisplayName("논리 삭제된 댓글은 댓글 목록에 조회되지 않는다")
        void findComments_excludeDeletedComment() throws Exception {
            // given
            User user = saveUser("soft@test.com", "softUser");
            Book book = saveBook("9788966262294");
            Review review = saveReview(user, book);
            Comment comment = saveComment(review.getId(), user.getId(), "삭제 대상");

            mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                            .header("Deokhugam-Request-User-ID", user.getId().toString()))
                    .andExpect(status().isNoContent());

            // when & then
            mockMvc.perform(get("/api/comments")
                            .param("reviewId", review.getId().toString())
                            .param("direction", "DESC")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(0));
        }
    }

    @Nested
    @DisplayName("10. 대시보드 인기 집계")
    class DashboardRanking {

        @Test
        @DisplayName("인기 리뷰 목록을 조회한다")
        void findPopularReviews_success() throws Exception {
            // given
            User user = saveUser("popular@test.com", "popularUser");
            Book book = saveBook("9788966262295");
            saveReview(user, book);

            // when & then
            mockMvc.perform(get("/api/reviews/popular")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("인기 도서 목록을 조회한다")
        void findPopularBooks_success() throws Exception {
            // given
            saveBook("9788966262296");

            // when & then
            mockMvc.perform(get("/api/books/popular")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    private User saveUser(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password("Password1!")
                .build();

        return userRepository.save(user);
    }

    private Book saveBook(String isbn) {
        Book book = Book.builder()
                .title("통합 테스트 도서")
                .author("테스트 저자")
                .isbn(isbn)
                .description("통합 테스트용 도서")
                .publisher("테스트 출판사")
                .publishedDate(LocalDate.of(2024, 1, 1))
                .build();

        return bookRepository.save(book);
    }

    private Review saveReview(User user, Book book) {
        Review review = Review.builder()
                .user(user)
                .book(book)
                .content("통합 테스트 리뷰")
                .rating(5)
                .build();

        return reviewRepository.save(review);
    }

    private Comment saveComment(UUID reviewId, UUID userId, String content) {
        Comment comment = Comment.builder()
                .reviewId(reviewId)
                .userId(userId)
                .content(content)
                .status(CommentStatus.ACTIVE)
                .build();

        return commentRepository.save(comment);
    }
}