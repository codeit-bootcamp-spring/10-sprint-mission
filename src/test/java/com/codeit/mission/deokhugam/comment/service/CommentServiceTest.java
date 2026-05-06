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
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CommentMapper commentMapper;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private CommentService commentService;

  private UUID userId;
  private UUID reviewId;
  private UUID commentId;
  private String userNickname;
  private Comment comment;
  private User user;
  private Review review;
  private CommentDto commentDto;
  private CommentFindAllRequest findAllRequest;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();
    commentId = UUID.randomUUID();
    userNickname = "testUser";

    comment = Comment.builder()
        .reviewId(reviewId)
        .userId(userId)
        .content("test content")
        .status(CommentStatus.ACTIVE)
        .build();

    ReflectionTestUtils.setField(comment, "id", commentId);

    Instant instant = LocalDateTime.of(2026, 4, 21, 10, 0, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();

    ReflectionTestUtils.setField(
        comment,
        "createdAt",
        instant
    );
  }

  @Test
  @DisplayName("댓글 생성 성공")
  void createCommentSuccess() {
    // given
    CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "test content");

    Review review = mock(Review.class);
    given(review.getId()).willReturn(reviewId);
    given(review.getStatus()).willReturn(ReviewStatus.ACTIVE);

    User reviewUser = mock(User.class);
    given(reviewUser.getId()).willReturn(UUID.randomUUID());
    given(review.getUser()).willReturn(reviewUser);

    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);
    given(user.getNickname()).willReturn(userNickname);

    Comment savedComment = Comment.builder()
            .reviewId(reviewId)
            .userId(userId)
            .content("test content")
            .status(CommentStatus.ACTIVE)
            .build();

    CommentDto commentDto = mock(CommentDto.class);

    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(commentRepository.saveAndFlush(any(Comment.class))).willReturn(savedComment);
    given(commentMapper.toDto(savedComment, userNickname)).willReturn(commentDto);

    // when
    CommentDto result = commentService.createComment(request);

    // then
    assertThat(result).isEqualTo(commentDto);
    verify(reviewRepository).findById(reviewId);
    verify(userRepository).findById(userId);
    verify(commentRepository).saveAndFlush(any(Comment.class));
    verify(reviewRepository).incrementCommentCount(reviewId);
    verify(eventPublisher).publishEvent(any(CommentRegisteredEvent.class));
    verify(commentMapper).toDto(savedComment, userNickname);
  }

  @Test
  @DisplayName("댓글 생성 실패 - 리뷰가 존재하지 않음")
  void createCommentFailByReviewNotFound() {
    // given
    CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "test content");
    given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.createComment(request))
        .isInstanceOf(ReviewNotFoundException.class);

    verify(reviewRepository).findById(reviewId);
    verify(userRepository, never()).findById(any());
    verify(commentRepository, never()).saveAndFlush(any());
    verify(reviewRepository, never()).incrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 생성 실패 - 유저가 존재하지 않음")
  void createCommentFailByUserNotFound() {
    // given
    CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "test content");

    Review review = mock(Review.class);
    given(review.getStatus()).willReturn(ReviewStatus.ACTIVE);

    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.createComment(request))
            .isInstanceOf(UserNotFoundException.class);

    verify(commentRepository, never()).saveAndFlush(any());
    verify(reviewRepository, never()).incrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 생성 실패 - 유저가 삭제 상태")
  void createCommentFailByDeletedUser() {
    // given
    CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "test content");

    Review review = mock(Review.class);
    given(review.getStatus()).willReturn(ReviewStatus.ACTIVE);

    User deletedUser = mock(User.class);
    given(deletedUser.getId()).willReturn(userId);
    given(deletedUser.getStatus()).willReturn(UserStatus.DELETED);

    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
    given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

    // when & then
    assertThatThrownBy(() -> commentService.createComment(request))
        .isInstanceOf(UserNotFoundException.class);

    verify(reviewRepository).findById(reviewId);
    verify(userRepository).findById(userId);
    verify(commentRepository, never()).saveAndFlush(any());
    verify(reviewRepository, never()).incrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void updateCommentSuccess() {
    // given
    CommentUpdateRequest request = new CommentUpdateRequest("updated content");

    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);
    given(user.getNickname()).willReturn(userNickname);

    CommentDto commentDto = mock(CommentDto.class);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(commentRepository.save(comment)).willReturn(comment);
    given(commentMapper.toDto(comment, userNickname)).willReturn(commentDto);

    // when
    CommentDto result = commentService.updateComment(commentId, userId, request);

    // then
    assertThat(result).isEqualTo(commentDto);
    assertThat(comment.getContent()).isEqualTo("updated content");
    verify(userRepository).findById(userId);
    verify(commentRepository).findById(commentId);
    verify(commentRepository).save(comment);
    verify(commentMapper).toDto(comment, userNickname);
  }

  @Test
  @DisplayName("댓글 수정 실패 - 댓글 존재하지 않음")
  void updateCommentFailByCommentNotFound() {
    // given
    CommentUpdateRequest request = new CommentUpdateRequest("updated content");

    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.updateComment(commentId, userId, request))
        .isInstanceOf(CommentNotFoundException.class);

    verify(userRepository).findById(userId);
    verify(commentRepository).findById(commentId);
    verify(commentRepository, never()).save(any());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 작성자 불일치")
  void updateCommentFailByAuthorMismatch() {
    // given
    UUID otherUserId = UUID.randomUUID();
    CommentUpdateRequest request = new CommentUpdateRequest("updated content");

    User otherUser = mock(User.class);
    given(otherUser.getStatus()).willReturn(UserStatus.ACTIVE);

    given(userRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));
    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

    // when & then
    assertThatThrownBy(() -> commentService.updateComment(commentId, otherUserId, request))
        .isInstanceOf(CommentAuthorException.class);

    verify(userRepository).findById(otherUserId);
    verify(commentRepository).findById(commentId);
    verify(commentRepository, never()).save(any());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 요청 유저가 존재하지 않음")
  void updateCommentFailByUserNotFound() {
    // given
    CommentUpdateRequest request = new CommentUpdateRequest("updated content");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.updateComment(commentId, userId, request))
            .isInstanceOf(UserNotFoundException.class);

    verify(commentRepository, never()).findById(any());
    verify(commentRepository, never()).save(any());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 댓글이 삭제 상태")
  void updateCommentFailByDeletedComment() {
    // given
    CommentUpdateRequest request = new CommentUpdateRequest("updated content");

    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);

    Comment deletedComment = Comment.builder()
            .reviewId(reviewId)
            .userId(userId)
            .content("deleted content")
            .status(CommentStatus.DELETED)
            .build();

    ReflectionTestUtils.setField(deletedComment, "id", commentId);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(commentRepository.findById(commentId)).willReturn(Optional.of(deletedComment));

    // when & then
    assertThatThrownBy(() -> commentService.updateComment(commentId, userId, request))
            .isInstanceOf(CommentNotFoundException.class);

    verify(commentRepository, never()).save(any());
  }

  @Test
  @DisplayName("댓글 상세 조회 성공")
  void findCommentSuccess() {
    // given
    User user = mock(User.class);
    given(user.getNickname()).willReturn(userNickname);

    CommentDto commentDto = mock(CommentDto.class);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(commentMapper.toDto(comment, userNickname)).willReturn(commentDto);

    // when
    CommentDto result = commentService.findComment(commentId);

    // then
    assertThat(result).isEqualTo(commentDto);
    verify(commentRepository).findById(commentId);
    verify(userRepository).findById(userId);
    verify(commentMapper).toDto(comment, userNickname);
  }

  @Test
  @DisplayName("댓글 상세 조회 실패 - 댓글 존재하지 않음")
  void findCommentFail() {
    // given
    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.findComment(commentId))
        .isInstanceOf(CommentNotFoundException.class);

    verify(commentRepository).findById(commentId);
    verify(userRepository, never()).findById(any());
  }

  @Test
  @DisplayName("댓글 상세 조회 실패 - 댓글이 삭제 상태")
  void findCommentFailByDeletedComment() {
    // given
    Comment deletedComment = Comment.builder()
            .reviewId(reviewId)
            .userId(userId)
            .content("deleted content")
            .status(CommentStatus.DELETED)
            .build();

    ReflectionTestUtils.setField(deletedComment, "id", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(deletedComment));

    // when & then
    assertThatThrownBy(() -> commentService.findComment(commentId))
            .isInstanceOf(CommentNotFoundException.class);

    verify(userRepository, never()).findById(any());
    verify(commentMapper, never()).toDto(any(), any());
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 마지막 페이지")
  void findAllCommentsSuccessLastPage() {
    // given
    CommentFindAllRequest findAllRequest = mock(CommentFindAllRequest.class);

    Review review = mock(Review.class);
    given(review.getStatus()).willReturn(ReviewStatus.ACTIVE);

    UUID secondUserId = UUID.randomUUID();
    UUID secondCommentId = UUID.randomUUID();

    User firstUser = mock(User.class);
    given(firstUser.getId()).willReturn(userId);
    given(firstUser.getNickname()).willReturn(userNickname);

    User secondUser = mock(User.class);
    given(secondUser.getId()).willReturn(secondUserId);
    given(secondUser.getNickname()).willReturn("secondUser");

    Comment secondComment = Comment.builder()
        .reviewId(reviewId)
        .userId(secondUserId)
        .content("second content")
        .status(CommentStatus.ACTIVE)
        .build();

    ReflectionTestUtils.setField(secondComment, "id", secondCommentId);

    Instant instant = LocalDateTime.of(2026, 4, 21, 9, 59, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();
    ReflectionTestUtils.setField(
        secondComment,
        "createdAt",
        instant
    );

    CommentDto firstCommentDto = mock(CommentDto.class);
    CommentDto secondCommentDto = mock(CommentDto.class);

    given(findAllRequest.reviewId()).willReturn(reviewId);
    given(findAllRequest.limit()).willReturn(2);

    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
    given(commentRepository.findAllByCursor(findAllRequest))
        .willReturn(List.of(comment, secondComment));
    given(commentRepository.countByReviewIdAndStatus(reviewId, CommentStatus.ACTIVE)).willReturn(2L);
    given(userRepository.findAllById(any()))
        .willReturn(List.of(firstUser, secondUser));

    given(commentMapper.toDto(comment, userNickname)).willReturn(firstCommentDto);
    given(commentMapper.toDto(secondComment, "secondUser")).willReturn(secondCommentDto);

    // when
    CursorPageResponseCommentDto result = commentService.findAllComments(findAllRequest);

    // then
    assertThat(result.content()).isEqualTo(List.of(firstCommentDto, secondCommentDto));
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursor()).isNull();
    assertThat(result.nextAfter()).isNull();
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 다음 페이지 존재")
  void findAllCommentsSuccessHasNext() {
    // given
    CommentFindAllRequest findAllRequest = mock(CommentFindAllRequest.class);

    Review review = mock(Review.class);
    given(review.getStatus()).willReturn(ReviewStatus.ACTIVE);

    UUID secondUserId = UUID.randomUUID();
    UUID thirdUserId = UUID.randomUUID();
    UUID secondCommentId = UUID.randomUUID();
    UUID thirdCommentId = UUID.randomUUID();

    User firstUser = mock(User.class);
    given(firstUser.getId()).willReturn(userId);
    given(firstUser.getNickname()).willReturn(userNickname);

    User secondUser = mock(User.class);
    given(secondUser.getId()).willReturn(secondUserId);
    given(secondUser.getNickname()).willReturn("secondUser");

    Comment secondComment = Comment.builder()
        .reviewId(reviewId)
        .userId(secondUserId)
        .content("second content")
        .status(CommentStatus.ACTIVE)
        .build();

    Comment thirdComment = Comment.builder()
        .reviewId(reviewId)
        .userId(thirdUserId)
        .content("third content")
        .status(CommentStatus.ACTIVE)
        .build();
    Instant secondCreatedAt = LocalDateTime.of(2026, 4, 21, 9, 59, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();
    Instant thirdCreatedAt = LocalDateTime.of(2026, 4, 21, 9, 58, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toInstant();

    ReflectionTestUtils.setField(secondComment, "id", secondCommentId);
    ReflectionTestUtils.setField(secondComment, "createdAt", secondCreatedAt);
    ReflectionTestUtils.setField(thirdComment, "id", thirdCommentId);
    ReflectionTestUtils.setField(thirdComment, "createdAt", thirdCreatedAt);

    CommentDto firstCommentDto = mock(CommentDto.class);
    CommentDto secondCommentDto = mock(CommentDto.class);

    given(findAllRequest.reviewId()).willReturn(reviewId);
    given(findAllRequest.limit()).willReturn(2);

    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
    given(commentRepository.findAllByCursor(findAllRequest))
        .willReturn(List.of(comment, secondComment, thirdComment));
    given(commentRepository.countByReviewIdAndStatus(reviewId, CommentStatus.ACTIVE)).willReturn(3L);
    given(userRepository.findAllById(any()))
        .willReturn(List.of(firstUser, secondUser));

    given(commentMapper.toDto(comment, userNickname)).willReturn(firstCommentDto);
    given(commentMapper.toDto(secondComment, "secondUser")).willReturn(secondCommentDto);

    // when
    CursorPageResponseCommentDto result = commentService.findAllComments(findAllRequest);

    // then
    assertThat(result.content()).isEqualTo(List.of(firstCommentDto, secondCommentDto));
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.totalElements()).isEqualTo(3);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursor()).isEqualTo(secondCommentId.toString());
    assertThat(result.nextAfter()).isEqualTo(secondCreatedAt);
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 리뷰 정보 없음")
  void findAllCommentsFailByReviewNotFound() {
    // given
    CommentFindAllRequest findAllRequest = mock(CommentFindAllRequest.class);

    given(findAllRequest.reviewId()).willReturn(reviewId);
    given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.findAllComments(findAllRequest))
        .isInstanceOf(ReviewNotFoundException.class);

    verify(reviewRepository).findById(reviewId);
    verify(commentRepository, never()).findAllByCursor(any());
  }

  @Test
  @DisplayName("댓글 논리 삭제 성공")
  void softDeleteCommentSuccess() {
    // given
    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    commentService.softDelete(commentId, userId);

    // then
    assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED);
    verify(commentRepository).findById(commentId);
    verify(userRepository).findById(userId);
    verify(reviewRepository).decrementCommentCount(reviewId);
    verify(commentRepository, never()).deleteById(any(UUID.class));
  }

  @Test
  @DisplayName("댓글 논리 삭제 실패 - 작성자 불일치")
  void softDeleteCommentFail() {
    // given
    UUID otherUserId = UUID.randomUUID();

    User otherUser = mock(User.class);
    given(otherUser.getStatus()).willReturn(UserStatus.ACTIVE);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(userRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));

    // when & then
    assertThatThrownBy(() -> commentService.softDelete(commentId, otherUserId))
        .isInstanceOf(CommentAuthorException.class);

    verify(commentRepository).findById(commentId);
    verify(userRepository).findById(otherUserId);
    verify(reviewRepository, never()).decrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 물리 삭제 성공")
  void hardDeleteCommentSuccess() {
    // given
    User user = mock(User.class);
    given(user.getStatus()).willReturn(UserStatus.ACTIVE);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    commentService.hardDelete(commentId, userId);

    // then
    verify(commentRepository).findById(commentId);
    verify(userRepository).findById(userId);
    verify(commentRepository).deleteById(commentId);
    verify(reviewRepository).decrementCommentCount(reviewId);
  }

  @Test
  @DisplayName("댓글 논리 삭제 실패 - 댓글이 존재하지 않음")
  void softDeleteFailByCommentNotFound() {
    // given
    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.softDelete(commentId, userId))
            .isInstanceOf(CommentNotFoundException.class);

    verify(userRepository, never()).findById(any());
    verify(reviewRepository, never()).decrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 물리 삭제 실패 - 작성자 불일치")
  void hardDeleteCommentFail() {
    // given
    UUID otherUserId = UUID.randomUUID();

    User otherUser = mock(User.class);
    given(otherUser.getStatus()).willReturn(UserStatus.ACTIVE);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(userRepository.findById(otherUserId)).willReturn(Optional.of(otherUser));

    // when & then
    assertThatThrownBy(() -> commentService.hardDelete(commentId, otherUserId))
        .isInstanceOf(CommentAuthorException.class);

    verify(commentRepository).findById(commentId);
    verify(userRepository).findById(otherUserId);
    verify(commentRepository, never()).deleteById(any(UUID.class));
    verify(reviewRepository, never()).decrementCommentCount(any());
  }

  @Test
  @DisplayName("댓글 물리 삭제 실패 - 댓글이 존재하지 않음")
  void hardDeleteFailByCommentNotFound() {
    // given
    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commentService.hardDelete(commentId, userId))
            .isInstanceOf(CommentNotFoundException.class);

    verify(userRepository, never()).findById(any());
    verify(commentRepository, never()).deleteById(any());
    verify(reviewRepository, never()).decrementCommentCount(any());
  }
}