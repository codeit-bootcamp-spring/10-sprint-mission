package com.codeit.mission.deokhugam.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.book.repository.BookRepository;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.repository.ReviewLikeRepository;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import com.codeit.mission.deokhugam.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "cloud.aws.credentials.access-key=dummy-access-key",
    "cloud.aws.credentials.secret-key=dummy-secret-key",
    "cloud.aws.region.static=ap-northeast-2",
    "cloud.aws.s3.bucket=dummy-bucket"
})
public class ReviewConcurrencyTest {

  @MockitoBean
  private S3Client s3Client;

  @MockitoBean
  private S3Presigner s3Presigner;

  @Autowired
  private ReviewService reviewServiceImplement;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private ReviewLikeRepository reviewLikeRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  // 테스트 직후 데이터 초기화
  @AfterEach
  public void tearDown() {
    reviewLikeRepository.deleteAllInBatch();
    reviewRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  // [성공]
  @Test
  @DisplayName("동시성 제어: 서로 다른 100명의 사용자가 동시에 좋아요를 누른 경우, 좋아요 수로 100을 반환")
  void add_review_concurrent_success() throws InterruptedException {
    // given
    int threadCount = 100;

    // 가짜 객체 | 도서 및 저자
    User author = User.builder()
        .nickname("author")
        .email("author@test.com")
        .password("password")
        .build();
    userRepository.save(author);

    Book book = Book.builder()
        .title("book")
        .author(author.getNickname())
        .isbn("1234567890")
        .publishedDate(LocalDate.now())
        .publisher("codeit")
        .description("holymoly")
        .build();
    bookRepository.save(book);

    // 좋아요를 추가할 리뷰 정보
    Review savedReview = Review.builder()
        .book(book)
        .user(author)
        .content("돌덩이 외게인이 뭐가 재밌다고 난리야")
        .rating(3)
        .build();
    reviewRepository.save(savedReview);

    // 동시에 좋아요를 누를 사용자 목록
    List<User> likedUsers = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      User targetUser = User.builder()
          .nickname("user" + i)
          .email("user" + i + "@test.com")
          .password("password")
          .build();

      likedUsers.add(userRepository.save(targetUser));
    }

    // 32개 스레드가 동시 수행 가능한 환경 풀 (Pool)
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 모든 작업이 끝날 때까지 메인 스레드를 대기시키는 객체
    CountDownLatch latch = new CountDownLatch(threadCount);
    // 멀티 스레드 환경에서 실패 횟수를 집계하기 위한 원자적 변수
    AtomicInteger failureCount = new AtomicInteger(0);

    // when
    for (int i = 0; i < threadCount; i++) {
      User liker = likedUsers.get(i);

      executorService.submit(() -> {
        try {
          // 각 스레드가 동시에 좋아요 추가 요청 수행
          reviewServiceImplement.toggleLike(savedReview.getId(), liker.getId());
        } catch (Exception e) {
          // 예외 발생 시, 실패 카운트 증가
          failureCount.incrementAndGet();
        } finally {
          // latch 카운트 1 감소
          latch.countDown();
        }
      });
    }

    // 모든 작업이 완료될 때까지 30초 대기 (무한 대기 방지)
    boolean completed = latch.await(30, TimeUnit.SECONDS);
    // 스레드 풀 닫기 (자원 누수 방지)
    executorService.shutdown();

    // 타임아웃 발생 시, 테스트 실패
    if (!completed) {
      throw new AssertionError("Test Timeout: Tasks did not complete within 30 seconds.");
    }

    // then
    assertEquals(0, failureCount.get(), "Some like requests failed during execution.");

    Review updatedReview = reviewRepository.findById(savedReview.getId()).orElseThrow();
    assertEquals(threadCount, updatedReview.getLikeCount());
  }
}