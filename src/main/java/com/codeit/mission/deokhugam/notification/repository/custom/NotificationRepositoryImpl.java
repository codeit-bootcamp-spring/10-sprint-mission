package com.codeit.mission.deokhugam.notification.repository.custom;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationRequestQuery;
import com.codeit.mission.deokhugam.notification.entity.Notification;
import com.codeit.mission.deokhugam.notification.entity.QNotification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  QNotification notification = QNotification.notification;

  @Override
  public Slice<Notification> findByUserWithCursor(UUID userId, NotificationRequestQuery query) {

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(notification.user.id.eq(userId));

    // after (createdAt 기준 커서)
    if (query.after() != null) {
      Instant afterTime = query.after();

      if (isDesc(query)) {
        builder.and(notification.createdAt.lt(afterTime));
      } else {
        builder.and(notification.createdAt.gt(afterTime));
      }
    }

    // cursor
    if (query.cursor() != null) {
      Instant cursorTime = query.cursor();

      if (isDesc(query)) {
        builder.and(notification.createdAt.lt(cursorTime));
      } else {
        builder.and(notification.createdAt.gt(cursorTime));
      }
    }

    List<Notification> result = queryFactory
      .selectFrom(notification)
      .where(builder)
      .orderBy(getOrderSpecifier(query))
      .limit(query.limit() + 1)
      .fetch();

    boolean hasNext = result.size() > query.limit();

    if (hasNext) {
      result = result.subList(0, query.limit());
    }

    return new SliceImpl<>(result, PageRequest.of(0, query.limit()), hasNext);
  }

  @Override
  public long countByUserId(UUID userId) {
    Long count = queryFactory
      .select(notification.count())
      .from(notification)
      .where(notification.user.id.eq(userId))
      .fetchOne();

    return count == null ? 0L : count;
  }

  private boolean isDesc(NotificationRequestQuery query) {
    return query.direction() == null || query.direction().equals(DirectionEnum.DESC);
  }

  private OrderSpecifier<?> getOrderSpecifier(NotificationRequestQuery query) {
    return isDesc(query)
      ? notification.createdAt.desc()
      : notification.createdAt.asc();
  }

}
