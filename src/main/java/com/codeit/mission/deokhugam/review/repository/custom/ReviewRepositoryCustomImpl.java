package com.codeit.mission.deokhugam.review.repository.custom;

import com.codeit.mission.deokhugam.book.entity.BookStatus;
import com.codeit.mission.deokhugam.review.dto.request.ReviewSearchConditionDto;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.exception.InvalidCursorFormatException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static com.codeit.mission.deokhugam.review.entity.QReview.review;

/*
    ReviewRepositoryCustomImpl
    --------------------------
    키워드 부분 일치 / 완전 일치에 따른 가중치 부여
    가중치를 최우선으로 정렬
    사용자가 요청한 정렬 조건 및 정렬 방향에 따른 정렬
 */
@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  // 필터링 + 내림차순 정렬 + 커서 기반 페이지네이션이 적용된 리뷰 목록 조회
  @Override
  public List<Review> searchReviews(ReviewSearchConditionDto condition) {
    // 1. 키워드 검색 조건
    BooleanBuilder filterBuilder = buildFilterCondition(condition);

    // 2. 커서 기반 페이지네이션 조건

    // 사용자가 요청한 정렬 조건 (orderBy): 기본값이 생성 시점이므로 평점만 비교
    boolean isRatingOrder = "rating".equals(
        condition.orderBy());

    // 사용자가 요청한 정렬 방향 (direction): 기본값은 내림차순 (desc)
    boolean isAsc = "asc".equalsIgnoreCase(
        condition.direction());

    // 사용자가 요청한 정렬 조건 및 정렬 방향이 반영된 빌더 객체
    BooleanBuilder cursorBuilder = buildCountCondition(condition, isRatingOrder, isAsc);
    filterBuilder.and(cursorBuilder);

    // 3. 정렬 조건
    OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(condition, isRatingOrder, isAsc);

    // 4. 동적 쿼리 실행
    List<Review> result = jpaQueryFactory
        .selectFrom(review)
        // 성능 최적화: N + 1 문제 방지
        .leftJoin(review.book).fetchJoin()
        .leftJoin(review.user).fetchJoin()
        .where(filterBuilder)
        // 평점 -> 생성 시간 -> id 내림차순 적용
        .orderBy(orderSpecifiers)
        .limit(condition.limit() + 1)
        .fetch();

    // 5. 로그 기록
    log.info("[SEARCH_REVIEWS] Find Reviews Keyword = {} OrderBy = {}, Review Size = {}",
        condition.keyword(),
        condition.orderBy(),
        result.size()
    );

    return result;
  }

  // 페이지네이션의 커서 조건을 정의하는 메서드
  private BooleanBuilder buildCountCondition(ReviewSearchConditionDto condition,
      boolean isRatingOrder, boolean isAsc) {
    // 1. 첫 페이지 요청: cursor 및 after이 항상 null
    if (condition.cursor() == null && condition.after() == null) {
      return new BooleanBuilder();
    }
    // 첫페이지 요청 이후, 잘못된 형식의 커서 전달
    if (condition.cursor() == null || condition.after() == null) {
      throw new InvalidCursorFormatException();
    }

    // 2. 커서 기반 페이지네이션 조건 빌더 객체 생성
    try {
      String cursorStr = condition.cursor();              // 커서 문자열
      Integer cursorRank = null;                          // 가중치: 키워드 일치 정도에 따른 가중치

      // 2. 전달된 커서 (가중치 + 정렬 조건 + ID) 중 가중치 (rank) 분리
      if (StringUtils.hasText(condition.keyword())) {
        String[] parts = cursorStr.split("_", 2);
        cursorRank = Integer.parseInt(parts[0]);

        // 잘못된 가중치가 부여된 경우, 예외 처리
        if (cursorRank != 1 && cursorRank != 2) {
          throw new InvalidCursorFormatException();
        }

        // 가중치 로그 기록
        log.info("[SEARCH_REVIEWS] Parsed Rank Weight: {}%", (cursorRank == 1 ? 100 : 50));

        cursorStr = parts[1];                             // 남은 커서 부분
      }

      // 3. 사용자의 정렬 조건에 맞는 빌더 객체 생성
      BooleanBuilder baseBuilder = isRatingOrder
          ? createRatingCursorBuilder(cursorStr, condition.after(), isAsc)
          : createTimeCursorBuilder(cursorStr, condition.after(), isAsc);

      // 4. 정렬 조건에 가중치 적용
      return applyRankCondition(baseBuilder, cursorRank, condition.keyword());

    } catch (RuntimeException e) {
      // 잘못된 커서 형식 입력 시, 예외 발생
      throw new InvalidCursorFormatException();
    }
  }

  // 평점 기준 정렬 빌더 생성
  private BooleanBuilder createRatingCursorBuilder(String remainingCursor, Instant after,
      boolean isAsc) {
    // 1. 커서 (정렬 기준 + ID) 중 평점과 마지막 요소 ID 분리
    String[] parts = remainingCursor.split("_", 2);

    // 분리된 항목이 2개가 아닐 경우, 잘못된 형식의 커서 예외 반환
    if (parts.length != 2) {
      throw new InvalidCursorFormatException();
    }

    int cursorRating = Integer.parseInt(parts[0]);
    UUID cursorId = UUID.fromString(parts[1]);

    // 2. 평점을 기준 정렬로 하는 빌더 객체 생성
    BooleanBuilder builder = new BooleanBuilder();

    if (isAsc) {
      // 마지막 요소의 평점보다 높은 요소, 평점은 같지만 생성 시간이 최근인 요소, 평점과 생성 시간이 같지만 ID 값이 큰 요소
      builder.or(review.rating.gt(cursorRating));
      builder.or(review.rating.eq(cursorRating).and(review.createdAt.gt(after)));
      builder.or(review.rating.eq(cursorRating).and(review.createdAt.eq(after))
          .and(review.id.gt(cursorId)));
    } else {
      // 마지막 요소의 평점보다 낮은 요소, 평점은 같지만 생성 시간이 오래된 요소, 평점과 생성 시간이 같지만 ID 값이 작은 요소
      builder.or(review.rating.lt(cursorRating));
      builder.or(review.rating.eq(cursorRating).and(review.createdAt.lt(after)));
      builder.or(review.rating.eq(cursorRating).and(review.createdAt.eq(after))
          .and(review.id.lt(cursorId)));
    }

    return builder;
  }

  // 생성 시간 기준 정렬 빌더 생성
  private BooleanBuilder createTimeCursorBuilder(String remainingCursor, Instant after,
      boolean isAsc) {
    // 1. 마지막 요소의 ID
    UUID cursorId = UUID.fromString(remainingCursor);

    // 2. 생성 시간을 기준 정렬로 하는 빌더 객체 생성
    BooleanBuilder builder = new BooleanBuilder();

    if (isAsc) {
      // 마지막 요소의 생성 시간보다 생성 시간이 최근인 요소, 생성 시간이 같지만 ID 값이 큰 요소
      builder.or(review.createdAt.gt(after));
      builder.or(review.createdAt.eq(after).and(review.id.gt(cursorId)));
    } else {
      // 마지막 요소의 생성 시간보다 생성 시간이 오래된 요소, 생성 시간이 같지만 ID 값이 작은 요소
      builder.or(review.createdAt.lt(after));
      builder.or(review.createdAt.eq(after).and(review.id.lt(cursorId)));
    }

    return builder;
  }

  // 가중치 결합
  private BooleanBuilder applyRankCondition(BooleanBuilder baseBuilder, Integer cursorRank,
      String keyword) {
    // 1. 최종 정렬 빌더 객체
    BooleanBuilder finalBuilder = new BooleanBuilder();

    if (cursorRank != null) {
      // 가중치를 최우선 정렬 기준으로 선정
      finalBuilder.or(exactMatchRank(keyword).gt(cursorRank));
      // 가중치가 같을 경우, 사용자가 요청한 정렬 기준 적용
      finalBuilder.or(exactMatchRank(keyword).eq(cursorRank).and(baseBuilder));
    } else {
      // 키워드가 없을 경우, 사용자가 요청한 정렬 기준 적용
      finalBuilder.and(baseBuilder);
    }

    return finalBuilder;
  }

  // 정렬 조건을 정의하는 메서드
  private OrderSpecifier<?>[] buildOrderSpecifiers(ReviewSearchConditionDto condition,
      boolean isRatingOrder, boolean isAsc) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    // 키워드가 존재할 경우, 가중치를 최우선으로 정렬
    if (StringUtils.hasText(condition.keyword())) {
      orderSpecifiers.add(exactMatchRank(condition.keyword()).asc());
    }

    // 정렬 기준이 평점인 경우
    if (isRatingOrder) {
      // 오름차순 여부에 따라 정렬 방향 결정
      orderSpecifiers.add(isAsc ? review.rating.asc() : review.rating.desc());
      orderSpecifiers.add(isAsc ? review.createdAt.asc() : review.createdAt.desc());
      orderSpecifiers.add(isAsc ? review.id.asc() : review.id.desc());
    }
    // 정렬 기준이 생성 시간인 경우
    else {
      orderSpecifiers.add(isAsc ? review.createdAt.asc() : review.createdAt.desc());
      orderSpecifiers.add(isAsc ? review.id.asc() : review.id.desc());
    }

    return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
  }

  // 완전 일치 및 부분 일치 가중치 계산
  private NumberExpression<Integer> exactMatchRank(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      // 키워드가 없으면 전부 동등하게 처리
      return Expressions.asNumber(1);
    }

    return new CaseBuilder()
        .when(review.book.title.equalsIgnoreCase(keyword)
            .or(review.user.isNotNull().and(review.user.nickname.equalsIgnoreCase(keyword)))
            .or(review.content.equalsIgnoreCase(keyword)))
        .then(1)           // 완전 일치 (1순위)
        .otherwise(2);  // 부분 일치 (2순위)
  }

  // 필터링 조건이 적용된 리뷰 목록의 전체 개수 조회
  @Override
  public long countWithFilter(ReviewSearchConditionDto condition) {
    // 1. 동적 쿼리 내 WHERE 절에 추가될 빌더 객체 생성
    BooleanBuilder filterBuilder = buildFilterCondition(condition);

    // 2. 동적 쿼리 실행
    Long totalCount = jpaQueryFactory
        .select(review.count())
        .from(review)
        .where(filterBuilder)
        .fetchOne();

    return totalCount != null ? totalCount : 0L;
  }

  // 동적 쿼리 필터링
  private BooleanBuilder buildFilterCondition(ReviewSearchConditionDto condition) {
    // 1. 동적 쿼리 내 WHERE 절에 추가될 빌더 객체 생성
    BooleanBuilder filterBuilder = new BooleanBuilder();

    // 2. 기본 상태 필터: 활성(ACTIVE)인 리뷰만 조회
    filterBuilder.and(review.status.eq(ReviewStatus.ACTIVE));
    filterBuilder.and(review.book.bookStatus.eq(BookStatus.ACTIVE));

    // 3. [완전 일치 조건]: 특정 도서 ID 필터
    if (condition.bookId() != null) {
      filterBuilder.and(review.book.id.eq(condition.bookId()));
    }

    // 4. [완전 일치 조건]: 특정 사용자 ID 필터
    if (condition.userId() != null) {
      filterBuilder.and(review.user.id.eq(condition.userId()));
    }

    // 5. [부분 일치 조건]: 키워드 필터
    filterBuilder.and(containsKeyword(condition.keyword()));

    return filterBuilder;
  }

  // 키워드 필터 빌더 객체 생성
  private BooleanBuilder containsKeyword(String keyword) {
    // 1. 키워드가 없으면 빈 빌더 객체 반환
    if (!StringUtils.hasText(keyword)) {
      return new BooleanBuilder();
    }

    // 2. 키워드가 있을 때만 OR 조건 조립 | 책 제목, 사용자 닉네임, 리뷰 내용
    return new BooleanBuilder()
        .or(review.content.containsIgnoreCase(keyword))
        .or(review.user.nickname.containsIgnoreCase(keyword))
        .or(review.book.title.containsIgnoreCase(keyword));
  }
}