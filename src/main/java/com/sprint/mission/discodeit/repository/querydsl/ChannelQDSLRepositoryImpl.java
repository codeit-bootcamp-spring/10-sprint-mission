package com.sprint.mission.discodeit.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.QChannel;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.entity.QReadStatus;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChannelQDSLRepositoryImpl implements ChannelQDSLRepository {

  private final JPAQueryFactory queryFactory;
  private final QChannel qChannel = QChannel.channel;
  private final QMessage qMessage = QMessage.message;
  private final QReadStatus qReadStatus = QReadStatus.readStatus;

  @Override
  public List<Channel> findVisibleToWithLastMessageAt(UUID userId) {
    return channelWithLastMessageAtQuery(isVisibleTo(userId))
        .stream()
        .map(this::mapToChannel)
        .toList();
  }

  @Override
  public Optional<Channel> findByIdWithLastMessageAt(UUID id) {
    Tuple result = channelWithLastMessageAtQuery(qChannel.id.eq(id)).fetchOne();
    return Optional.ofNullable(result).map(this::mapToChannel);
  }

  @Override
  public List<Channel> findAllWithLastMessageAt() {
    return channelWithLastMessageAtQuery(null)
        .fetch()
        .stream()
        .map(this::mapToChannel)
        .toList();
  }

  private BooleanExpression isVisibleTo(UUID userId) {
    return qChannel.type.eq(ChannelType.PUBLIC)
        .or(qChannel.id.in(joinedChannelIdsSubQuery(userId)));
  }

  private JPAQuery<Tuple> channelWithLastMessageAtQuery(BooleanExpression condition) {
    return queryFactory.select(qChannel, lastMessageAtSubQuery())
        .from(qChannel)
        .where(condition);
  }

  private Channel mapToChannel(Tuple tuple) {
    Channel channel = tuple.get(qChannel);
    Instant lastMessageAt = tuple.get(lastMessageAtSubQuery());
    Objects.requireNonNull(channel).setLastMessageAt(lastMessageAt);
    return channel;
  }

  private JPQLSubQuery<Instant> lastMessageAtSubQuery() {
    return JPAExpressions.select(qMessage.createdAt.max())
        .from(qMessage)
        .where(qMessage.channel.id.eq(qChannel.id));
  }

  private JPQLSubQuery<UUID> joinedChannelIdsSubQuery(UUID userId) {
    return JPAExpressions.select(qReadStatus.channel.id)
        .from(qReadStatus)
        .where(qReadStatus.user.id.eq(userId));
  }
}
