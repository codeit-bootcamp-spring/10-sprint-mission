package com.codeit.mission.deokhugam.review.repository.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.config.JpaAuditingConfig;
import com.codeit.mission.deokhugam.config.QuerydslConfig;
import com.codeit.mission.deokhugam.review.dto.request.ReviewSearchConditionDto;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.exception.InvalidCursorFormatException;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.entity.UserStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
class ReviewRepositoryCustomImplTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private JPAQueryFactory jpaQueryFactory;

  private ReviewRepositoryCustomImpl reviewRepositoryCustom;

  // 가짜 객체 | 도서 및 저자
  private Book savedBook;
  private User savedUser;

  // 초기화 세팅 메서드
  @BeforeEach
  void setUp() {
    // 매 테스트마다 커스텀 레파지토리 초기화
    reviewRepositoryCustom = new ReviewRepositoryCustomImpl(jpaQueryFactory);

    // 가짜 객체 | 도서
    Book book = Book.builder()
        .title("book")
        .author("author")
        .isbn("12345")
        .description("description")
        .publisher("publisher")
        .thumbnailUrl("http://...")
        .publishedDate(LocalDate.now())
        .build();
    ReflectionTestUtils.setField(book, "bookStatus", BookStatus.ACTIVE);

    entityManager.persist(book);            // 데이터 영속화
    savedBook = book;

    // 저자
    User user = User.builder()
        .nickname("test")
        .email("test@test.com")
        .password("1234")
        .build();
    ReflectionTestUtils.setField(user, "status", UserStatus.ACTIVE);

    entityManager.persist(user);
    savedUser = user;

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("첫 페이지 조회 성공: 키워드 및 커서없이 첫 페이지 조회")
  void searchReviews_FirstPage_NoKeyword() {
    // given
    Book book = entityManager.find(Book.class, savedBook.getId());
    User user = entityManager.find(User.class, savedUser.getId());

    // 리뷰 데이터 생성 및 저장
    Review review = Review.builder()
        .book(book)
        .user(user)
        .rating(5).
        content("good")
        .build();
    ReflectionTestUtils.setField(review, "status", ReviewStatus.ACTIVE);

    entityManager.persist(review);
    entityManager.flush();
    entityManager.clear();

    // 검색 조건 요청 DTO
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        null,
        null,
        null,
        "rating",
        "desc",
        null,
        null,
        10
    );

    // when
    List<Review> results = reviewRepositoryCustom.searchReviews(condition);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getContent()).isEqualTo("good");
  }

  @Test
  @DisplayName("키워드 검색 성공: 키워드 검색 및 가중치 (완전 일치 / 부분 일치) 정렬 테스트")
  void searchReviews_WithKeyword_And_Rank() {
    // given
    Book book = entityManager.find(Book.class, savedBook.getId());
    ReflectionTestUtils.setField(book, "title", "에로스와 안테로스");
    entityManager.persist(book);

    User user = entityManager.find(User.class, savedUser.getId());

    // 리뷰 | 도서 제목 완전 일치
    Review exactMatchReview = Review.builder()
        .book(book)
        .user(user)
        .rating(3)
        .content("bad")
        .build();
    ReflectionTestUtils.setField(exactMatchReview, "status", ReviewStatus.ACTIVE);
    ReflectionTestUtils.setField(exactMatchReview, "createdAt", Instant.now().minusSeconds(3600));
    entityManager.persist(exactMatchReview);

    // 리뷰 | 도서 제목 부분 일치
    Book otherBook = Book.builder()
        .title("book2")
        .author("author2")
        .isbn("54321")
        .description("description2")
        .publisher("publisher2")
        .thumbnailUrl("http://...")
        .publishedDate(LocalDate.now())
        .build();
    ReflectionTestUtils.setField(otherBook, "bookStatus", BookStatus.ACTIVE);
    entityManager.persist(otherBook);

    // 리뷰 | 내용에 키워드 포함
    Review partialMatchReview = Review.builder()
        .book(otherBook)
        .user(user)
        .rating(3)
        .content("그리스 로마 신화의 에로스와 안테로스")
        .build();
    ReflectionTestUtils.setField(partialMatchReview, "status", ReviewStatus.ACTIVE);
    ReflectionTestUtils.setField(partialMatchReview, "createdAt", Instant.now());
    entityManager.persist(partialMatchReview);

    entityManager.flush();
    entityManager.clear();

    // 검색 조건 요청 DTO (키워드 포함)
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        null,
        null,
        "에로스와 안테로스",
        "rating",
        "desc",
        null,
        null,
        10
    );

    // when
    List<Review> results = reviewRepositoryCustom.searchReviews(condition);

    // then:
    assertThat(results).hasSize(2);
    assertThat(results.get(0).getBook().getTitle()).isEqualTo("에로스와 안테로스");
    assertThat(results.get(1).getBook().getTitle()).isEqualTo("book2");
  }

  @Test
  @DisplayName("정렬 성공: 평점(rating) 기준 커서 페이지네이션 동작")
  void searchReviews_RatingCursor_Desc() {
    // given
    Book book = entityManager.find(Book.class, savedBook.getId());
    User user = entityManager.find(User.class, savedUser.getId());

    Review review1 = Review.builder()
        .book(book)
        .user(user)
        .rating(4)
        .content("good")
        .build();
    ReflectionTestUtils.setField(review1, "status", ReviewStatus.ACTIVE);
    entityManager.persist(review1);
    entityManager.flush();
    entityManager.clear();

    // 이전 페이지의 마지막 커서 (평점 5점_랜덤UUID)
    String cursor = "5_" + UUID.randomUUID().toString();
    Instant after = Instant.now();

    // 검색 조건 요청 DTO (정렬 조건: 평점)
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        null,
        null,
        null,
        "rating",
        "desc",
        cursor,
        after,
        10
    );

    // when
    List<Review> results = reviewRepositoryCustom.searchReviews(condition);

    // then
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getContent()).isEqualTo("good");
  }

  @Test
  @DisplayName("정렬 성공: 생성시간(createdAt) 기준 커서 페이지네이션 동작")
  void searchReviews_TimeCursor_Asc() {
    // given
    Book book = entityManager.find(Book.class, savedBook.getId());
    User user = entityManager.find(User.class, savedUser.getId());

    Review review = Review.builder()
        .book(book)
        .user(user)
        .rating(4)
        .content("good")
        .build();
    ReflectionTestUtils.setField(review, "status", ReviewStatus.ACTIVE);
    entityManager.persist(review);
    entityManager.flush();
    entityManager.clear();

    // 이전 페이지의 마지막 커서
    String cursor = UUID.randomUUID().toString();
    Instant after = Instant.now().minusSeconds(100);

    // 검색 조건 요청 DTO (정렬 조건: 생성 시간)
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        null,
        null,
        null,
        "createdAt",
        "asc",
        cursor,
        after,
        10
    );

    // when
    List<Review> results = reviewRepositoryCustom.searchReviews(condition);

    // then
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getContent()).isEqualTo("good");
  }

  @Test
  @DisplayName("정렬 실패: 잘못된 커서 형식을 입력한 경우, InvalidCursorFormatException 예외 발생")
  void searchReviews_InvalidCursor() {
    // given

    // 검색 조건 요청 DTO (잘못된 커서 값)
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        null,
        null,
        null,
        "rating",
        "desc",
        "wrong-cursor",
        null,
        10
    );

    // when & then
    assertThrows(InvalidCursorFormatException.class, () -> {
      reviewRepositoryCustom.searchReviews(condition);
    });
  }

  @Test
  @DisplayName("필터링 성공: 필터 조건에 맞는 리뷰 개수 조회")
  void countWithFilter_Success() {
    // given
    Book book = entityManager.find(Book.class, savedBook.getId());
    User user = entityManager.find(User.class, savedUser.getId());

    Review activeReview = Review.builder()
        .book(book)
        .user(user)
        .rating(5)
        .content("good")
        .build();
    ReflectionTestUtils.setField(activeReview, "status", ReviewStatus.ACTIVE);

    entityManager.persist(activeReview);
    entityManager.flush();
    entityManager.clear();

    // 검색 조건 요청 DTO
    ReviewSearchConditionDto condition = new ReviewSearchConditionDto(
        user.getId(),
        book.getId(),
        null,
        "rating",
        "desc",
        null,
        null,
        10
    );

    // when
    long count = reviewRepositoryCustom.countWithFilter(condition);

    // then
    assertThat(count).isEqualTo(1L);
  }
}