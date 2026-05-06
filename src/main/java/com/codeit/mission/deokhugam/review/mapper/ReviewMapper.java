package com.codeit.mission.deokhugam.review.mapper;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.review.dto.request.ReviewCreateRequest;
import com.codeit.mission.deokhugam.review.dto.response.CursorPageResponseReviewDto;
import com.codeit.mission.deokhugam.review.dto.response.ReviewDto;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.user.entity.User;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

  // 요청 DTO -> 엔티티 변환
  @Mapping(target = "book", source = "book")
  @Mapping(target = "user", source = "user")
  @Mapping(target = "content", source = "request.content")
  @Mapping(target = "rating", source = "request.rating")
  Review toEntity(ReviewCreateRequest request, Book book, User user);

  // Entity -> 응답 DTO 변환
  @Mapping(target = "id", source = "review.id")
  @Mapping(target = "bookId", source = "review.book.id")
  @Mapping(target = "bookTitle", source = "review.book.title")
  @Mapping(target = "bookThumbnailUrl", source = "review.book.thumbnailUrl")
  @Mapping(target = "userId", expression = "java(review.getUser() != null ? review.getUser().getId() : null)")
  @Mapping(target = "userNickName", expression = "java(review.getUser() != null ? review.getUser().getNickname() : \"알 수 없는 사용자\")")
  @Mapping(target = "likedByMe", source = "isLiked")
  ReviewDto toDto(Review review, boolean isLiked);

  // Entity List -> 응답 DTO List
  default List<ReviewDto> toDtoList(List<Review> reviews, List<UUID> likedReviewIds) {
    if (reviews == null) {
      return Collections.emptyList();
    }

    // 성능 개선을 위해 Set 자료 구조 활용
    Set<UUID> likedSet = likedReviewIds != null
        ? new HashSet<>(likedReviewIds)
        : Collections.emptySet();

    return reviews.stream()
        .map(review -> toDto(
            review,
            likedSet.contains(review.getId())))
        .toList();
  }

  // 응답 DTO -> 페이징 전용 응답 DTO 변환
  default CursorPageResponseReviewDto<ReviewDto> toCursorPageResponse(
      List<ReviewDto> content,
      String nextCursor,
      Instant nextAfter,
      int limit,
      long totalElements,
      boolean hasNext
  ) {
    return CursorPageResponseReviewDto.<ReviewDto>builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(limit)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }
}
