package com.codeit.mission.deokhugam.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationRequestQuery;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationUpdateRequest;
import com.codeit.mission.deokhugam.notification.dto.response.CursorPageResponseNotificationDto;
import com.codeit.mission.deokhugam.notification.dto.response.NotificationDto;
import com.codeit.mission.deokhugam.notification.entity.Notification;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private NotificationMapper notificationMapper; // = Mappers.getMapper(NotificationMapper.class);

  @InjectMocks
  private NotificationService notificationService;

  private User createUser(String nickname) {
    return User.builder()
      .nickname(nickname)
      .build();
  }

  private Review createReview(User user) {
    Book book = Book.builder()
      .title("제목")
      .rating(5)
      .build();

    return Review.builder()
      .user(user)
      .book(book)
      .content("리뷰 내용")
      .rating(5)
      .build();
  }

  @Nested
  @DisplayName("알림 등록")
  class RegistNotificationTest {

    @Test
    @DisplayName("좋아요에 의한 알림 생성 테스트")
    void createNotificationByLike() {
      // given
      UUID actorId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();

      User actor = createUser("좋아요 유저");
      User receiver = createUser("작성자");
      Review review = createReview(receiver);

      given(userRepository.findById(actorId)).willReturn(Optional.of(actor));
      given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
      given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

      // 로그 출력을 위해 save 정의 및 id 필드 설정
      given(notificationRepository.save(any(Notification.class)))
        .willAnswer(invocation -> {
          Notification n = invocation.getArgument(0);
          ReflectionTestUtils.setField(n, "id", UUID.randomUUID());
          return n;
        });

      // when
      notificationService.createByLike(actorId, receiverId, reviewId);

      // then
      ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository, times(1)).save(captor.capture());
      verifyNoMoreInteractions(notificationRepository);

      Notification saved = captor.getValue();

      assertThat(saved.getUser()).isSameAs(receiver);   // 동일 객체인지
      assertThat(saved.getReview()).isSameAs(review);
      assertThat(saved.getReviewContent()).isEqualTo("리뷰 내용");
      assertThat(saved.isConfirmed()).isFalse();
      assertThat(saved.getMessage())
        .isEqualTo("[" + actor.getNickname() + "]님이 나의 리뷰를 좋아합니다.");
    }

    @Test
    @DisplayName("댓글에 의한 알림 생성 테스트")
    void createNotificationByComment() {
      // given
      UUID actorId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();

      User actor = createUser("댓글 유저");
      User receiver = createUser("작성자");
      Review review = createReview(receiver);

      given(userRepository.findById(actorId)).willReturn(Optional.of(actor));
      given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
      given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

      given(notificationRepository.save(any(Notification.class)))
        .willAnswer(invocation -> {
          Notification n = invocation.getArgument(0);
          ReflectionTestUtils.setField(n, "id", UUID.randomUUID());
          return n;
        });

      // when
      notificationService.createByComment(actorId, receiverId, reviewId); // here

      // then
      ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
      verify(notificationRepository).save(captor.capture());

      Notification saved = captor.getValue();

      assertThat(saved.getUser()).isSameAs(receiver);   // 동일 객체인지
      assertThat(saved.getReview()).isSameAs(review);
      assertThat(saved.getReviewContent()).isEqualTo("리뷰 내용");
      assertThat(saved.isConfirmed()).isFalse();
      assertThat(saved.getMessage())
        .isEqualTo("[" + actor.getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.");
    }

    @Test
    @DisplayName("리뷰 랭킹 선정에 의한 알림 생성 테스트")
    void createNotificationByReviewRanked() {
      // given
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();
      List<UUID> reviewIds = List.of(reviewId);

      User receiver = createUser("작성자");
      Review review = createReview(receiver);

      given(reviewRepository.findByIdIn(reviewIds)).willReturn(List.of(review));

      // when
      notificationService.createByReviewRanked(reviewIds);

      // then
      ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
      verify(notificationRepository).saveAll(captor.capture());

      List<Notification> savedList = captor.getValue();

      // 저장된 알림 수 확인
      assertThat(savedList).hasSize(1);

      // 알림 객체 확인
      Notification saved = savedList.get(0);
      assertThat(saved.getUser()).isSameAs(receiver);   // 동일 객체인지
      assertThat(saved.getReview()).isSameAs(review);
      assertThat(saved.getReviewContent()).isEqualTo("리뷰 내용");
      assertThat(saved.isConfirmed()).isFalse();
      assertThat(saved.getMessage())
        .isEqualTo("나의 리뷰가 인기 리뷰로 등록되었습니다.");
    }

    @Test
    @DisplayName("actor 유저 정보 없음 실패")
    void createNotificationActorUserNotFound() {
      // given
      UUID actorId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();

      given(userRepository.findById(actorId)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() ->
        notificationService.createByLike(actorId, receiverId, reviewId)
      ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("receiver 유저 정보 없음 실패")
    void createNotificationReceiverUserNotFound() {
      // given
      UUID actorId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();

      User actor = createUser("actor");

      given(userRepository.findById(actorId)).willReturn(Optional.of(actor));
      given(userRepository.findById(receiverId)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() ->
        notificationService.createByLike(actorId, receiverId, reviewId)
      ).isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 정보 없음 실패")
    void createNotificationReviewNotFound() {
      // given
      UUID actorId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();

      User actor = createUser("작성자");
      User receiver = createUser("작성자");
      Review review = createReview(receiver);

      given(userRepository.findById(actorId)).willReturn(Optional.of(actor));
      given(userRepository.findById(receiverId)).willReturn(Optional.of(receiver));
      given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() ->
        notificationService.createByLike(actorId, receiverId, reviewId)
      ).isInstanceOf(ReviewNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("알림 목록 조회")
  class FindNotificationTest {

    private UUID userId;
    private NotificationRequestQuery query;

    @BeforeEach
    void setUp() {
      userId = UUID.randomUUID();

      query = NotificationRequestQuery.builder()
        .limit(1)
        .direction(DirectionEnum.DESC)
        .build();
    }

    // limit=1로 두개의 데이터 중 하나만 조회하는 경우를 테스트합니다.
    @Test
    @DisplayName("첫번째 페이지 조회 성공")
    void findFirstPageSuccess() {
      // given
      Notification n1 = mock(Notification.class);
      Notification n2 = mock(Notification.class);

      Instant time1 = Instant.now();
      Instant time2 = time1.minusSeconds(10);

      given(n1.getCreatedAt()).willReturn(time1);

      List<Notification> notifications = List.of(n1);

      Slice<Notification> slice =
        new SliceImpl<>(notifications, PageRequest.of(0, 1), true);

      given(notificationRepository.findByUserWithCursor(userId, query))
        .willReturn(slice);

      given(notificationRepository.countByUserId(userId))
        .willReturn(2L);

      NotificationDto dto1 = mock(NotificationDto.class);
      NotificationDto dto2 = mock(NotificationDto.class);

      given(notificationMapper.toDto(n1)).willReturn(dto1);

      // when
      CursorPageResponseNotificationDto result =
        notificationService.findByUserId(userId, query);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.size()).isEqualTo(1);
      assertThat(result.totalElements()).isEqualTo(2);
      assertThat(result.hasNext()).isTrue();
      assertThat(result.nextCursor()).isEqualTo(time1.toString());
      assertThat(result.nextAfter()).isEqualTo(time1);
    }

    @Test
    @DisplayName("조회 결과가 없을 시 nextCursor, nextAfter가 null인지 검증")
    void findByUserIdNoContent() {
      // given
      Slice<Notification> slice =
        new SliceImpl<>(List.of(), PageRequest.of(0, 2), false);

      given(notificationRepository.findByUserWithCursor(userId, query))
        .willReturn(slice);

      given(notificationRepository.countByUserId(userId))
        .willReturn(0L);

      // when
      CursorPageResponseNotificationDto result =
        notificationService.findByUserId(userId, query);

      // then
      assertThat(result.content()).isEmpty();
      assertThat(result.nextCursor()).isNull();
      assertThat(result.nextAfter()).isNull();
      assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("커서가 있는 경우 다음 페이지 조회 성공")
    void findByUserIdWithCursor() {
      // given
      Instant after = Instant.parse("2026-01-01T00:00:00Z");

      NotificationRequestQuery query = NotificationRequestQuery.builder()
        .after(after)
        .limit(2)
        .build();

      Notification n1 = mock(Notification.class);
      Notification n2 = mock(Notification.class);

      Instant t1 = LocalDateTime.of(2025, 12, 31, 23, 59, 50)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();

      Instant t2 = LocalDateTime.of(2025, 12, 31, 23, 59, 40)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();

      given(n2.getCreatedAt()).willReturn(t2);

      List<Notification> notifications = List.of(n1, n2);

      Slice<Notification> slice =
        new SliceImpl<>(notifications, PageRequest.of(0, 2), true);

      given(notificationRepository.findByUserWithCursor(userId, query))
        .willReturn(slice);

      given(notificationRepository.countByUserId(userId))
        .willReturn(50L);

      NotificationDto dto1 = mock(NotificationDto.class);
      NotificationDto dto2 = mock(NotificationDto.class);

      given(notificationMapper.toDto(n1)).willReturn(dto1);
      given(notificationMapper.toDto(n2)).willReturn(dto2);

      // when
      CursorPageResponseNotificationDto result =
        notificationService.findByUserId(userId, query);

      // then
      // Repository에 after 포함된 query 전달 확인
      verify(notificationRepository).findByUserWithCursor(userId, query);

      // nextCursor / nextAfter 계산 검증 (마지막 요소 기준)
      assertThat(result.nextAfter()).isEqualTo(t2);
      assertThat(result.nextCursor()).isEqualTo(t2.toString());

      // 기타 값 검증
      assertThat(result.content()).hasSize(2);
      assertThat(result.totalElements()).isEqualTo(50L);
      assertThat(result.hasNext()).isTrue();
    }

  }

  @Nested
  @DisplayName("알림 업데이트 테스트")
  class UpdateNotificationTest {

    @Test
    @DisplayName("알림 단건 업데이트 성공")
    void updateSingleNotification_Success() {
      // given
      UUID requestUserId = UUID.randomUUID();
      UUID reviewId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();

      // 알림을 조회할 유저
      User requestUser = User.builder().build();
      ReflectionTestUtils.setField(requestUser, "id", requestUserId);

      // 알림 대상이 된 리뷰
      Review review = Review.builder()
        .user(requestUser)
        .content("review content")
        .rating(5)
        .build();
      ReflectionTestUtils.setField(review, "id", reviewId);

      // 알림
      Notification notification = Notification.builder()
        .user(requestUser)
        .review(review)
        .reviewContent("review content")
        .message("noti message")
        .build();
      ReflectionTestUtils.setField(notification, "id", notificationId);

      // 알림을 읽음으로 갱신
      NotificationUpdateRequest requestDto = new NotificationUpdateRequest(true);

      // 응답 dto
      NotificationDto expectedDto = NotificationDto.builder()
        .userId(requestUserId)
        .confirmed(true)
        .reviewId(reviewId)
        .reviewContent("review content")
        .message("noti message")
        .build();

      given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));
      given(notificationMapper.toDto(notification)).willReturn(expectedDto);

      // when
      NotificationDto result = notificationService.updateById(notificationId, requestUserId,
        requestDto);

      // then
      assertThat(notification.isConfirmed()).isTrue();
      assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("알림 단건 업데이트 실패: 해당 유저가 소유하지 않은 알림 id를 전달한 경우")
    void updateSingleNotification_Failed() {
      // given
      UUID requestUserId = UUID.randomUUID();
      UUID receiverUserId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();

      // 알림을 조회할 유저
      User requestUser = User.builder().build();
      ReflectionTestUtils.setField(requestUser, "id", requestUserId);

      // 알림을 받은 유저
      User receiverUser = User.builder().build();
      ReflectionTestUtils.setField(receiverUser, "id", receiverUserId);

      // 알림
      Notification notification = Notification.builder()
        .user(receiverUser)
        .build();
      ReflectionTestUtils.setField(notification, "id", notificationId);

      // 알림을 읽음으로 갱신
      NotificationUpdateRequest requestDto = new NotificationUpdateRequest(true);

      given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

      // when & then
      // 예외 발생 검증
      assertThrows(NotificationNotOwnedException.class, () ->
        notificationService.updateById(notificationId, requestUserId, requestDto)
      );

      // 알림 상태가 갱신되지 않았음을 검증
      assertThat(notification.isConfirmed()).isFalse();
      verify(notificationMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("알림 복수 업데이트 성공: 특정 유저의 모든 알림을 읽음으로 처리")
    void updateAllByUserId_Success() {
      // given
      UUID userId = UUID.randomUUID();

      given(userRepository.existsById(userId)).willReturn(true);

      // when
      notificationService.updateByUserId(userId);

      // then
      verify(userRepository).existsById(userId);
      verify(notificationRepository).updateAllAsConfirmed(userId);
    }

    @Test
    @DisplayName("알림 복수 업데이트 실패: 존재하지 않는 유저의 id로 요청한 경우")
    void updateAllByUserId_Fail() {
      // given
      UUID userId = UUID.randomUUID();

      given(userRepository.existsById(userId)).willReturn(false);

      // when & then
      assertThatThrownBy(() ->
        notificationService.updateByUserId(userId)
      ).isInstanceOf(UserNotFoundException.class);

      // 핵심: update 쿼리는 실행되면 안 됨
      verify(notificationRepository, never()).updateAllAsConfirmed(any());
    }
  }


}
