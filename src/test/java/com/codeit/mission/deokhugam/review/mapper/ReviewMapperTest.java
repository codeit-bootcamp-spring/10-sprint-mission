package com.codeit.mission.deokhugam.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.mission.deokhugam.review.dto.response.ReviewDto;
import com.codeit.mission.deokhugam.review.entity.Review;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ReviewMapperTest {

  private final ReviewMapper reviewMapper = new ReviewMapperImpl();

  @Test
  @DisplayName("DTO 변환 성공: Review 엔티티 변환할 때, 모든 필드가 정상적으로 매핑된 응답 DTO가 반환")
  void toDto_Success() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.builder()
        .content("test")
        .rating(5)
        .build();
    ReflectionTestUtils.setField(review, "id", reviewId);

    boolean likedByMe = true;

    // when
    ReviewDto result = reviewMapper.toDto(review, likedByMe);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(reviewId);
    assertThat(result.content()).isEqualTo("test");
    assertThat(result.rating()).isEqualTo(5);
    assertThat(result.likedByMe()).isTrue();
  }

  @Test
  @DisplayName("DTO 목록 변환 성공: Review 리스트를 변환할 때, 좋아요 여부가 포함된 응답 DTO 리스트가 반환")
  void toDtoList_Success() {
    // given
    UUID reviewId = UUID.randomUUID();
    Review review = Review.builder()
        .content("test")
        .rating(4)
        .build();
    ReflectionTestUtils.setField(review, "id", reviewId);

    List<Review> reviews = List.of(review);
    List<UUID> likedReviewIds = List.of(reviewId);

    // when
    List<ReviewDto> resultList = reviewMapper.toDtoList(reviews, likedReviewIds);

    // then
    assertThat(resultList).hasSize(1);
    assertThat(resultList.get(0).likedByMe()).isTrue();
  }
}