package com.sprint.mission.discodeit.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.dto.ChannelDetailResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.QBinaryContent;
import com.sprint.mission.discodeit.entity.QChannel;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.entity.QReadStatus;
import com.sprint.mission.discodeit.entity.QUser;
import com.sprint.mission.discodeit.entity.QUserStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChannelQDSLRepositoryImpl implements ChannelQDSLRepository {

  private final JPAQueryFactory queryFactory;
  private final QChannel qChannel = QChannel.channel;
  private final QMessage qMessage = QMessage.message;
  private final QReadStatus qReadStatus = QReadStatus.readStatus;
  private final QUser qUser = QUser.user;
  private final QUserStatus qUserStatus = QUserStatus.userStatus;
  private final QBinaryContent qBinaryContent = QBinaryContent.binaryContent;

  @Override
  public List<ChannelDetailResponse> findVisibleChannelDetails(UUID userId) {
    return fetchChannelDetails(isVisibleTo(userId));
  }

  @Override
  public Optional<ChannelDetailResponse> findChannelDetailById(UUID id) {
    List<ChannelDetailResponse> result = fetchChannelDetails(qChannel.id.eq(id));
    if (result.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(result.get(0));
  }

  @Override
  public List<ChannelDetailResponse> findAllChannelDetails() {
    return fetchChannelDetails(null);
  }

  private List<ChannelDetailResponse> fetchChannelDetails(BooleanExpression condition) {
    Map<Channel, Instant> channelsWithLastMessageAt = findChannelsWithLastMessageTime(condition);
    if (channelsWithLastMessageAt.isEmpty()) {
      return List.of();
    }
    List<UUID> channelIds = channelsWithLastMessageAt.keySet()
        .stream()
        .map(Channel::getId)
        .toList();
    Map<UUID, List<User>> channelsWithParticipants = findParticipantsGroupedByChannel(channelIds);
    Function<Channel, ChannelDetailResponse> toChannelDetailResponse =
        channel -> new ChannelDetailResponse(
            channel,
            channelsWithLastMessageAt.get(channel),
            channelsWithParticipants.get(channel.getId())
        );
    return channelsWithLastMessageAt.keySet().stream()
        .map(toChannelDetailResponse)
        .toList();
  }

  private Map<UUID, List<User>> findParticipantsGroupedByChannel(List<UUID> channelIds) {
    return queryFactory.select(qReadStatus.channel.id, qUser)
        .from(qReadStatus)
        .join(qReadStatus.user, qUser)
        .leftJoin(qUser.profile, qBinaryContent).fetchJoin()
        .join(qUser.status, qUserStatus).fetchJoin()
        .where(qReadStatus.channel.id.in(channelIds))
        .fetch()
        .stream()
        .collect(Collectors.groupingBy(
            tuple -> tuple.get(qReadStatus.channel.id),
            Collectors.mapping(tuple -> tuple.get(qUser), Collectors.toList())
        ));
  }

  private Map<Channel, Instant> findChannelsWithLastMessageTime(BooleanExpression condition) {
    return queryFactory.select(qChannel, lastMessageAtSubQuery())
        .from(qChannel)
        .where(condition)
        .fetch()
        .stream()
        .collect(HashMap::new,
            (map, tuple)
                -> map.put(tuple.get(qChannel), tuple.get(lastMessageAtSubQuery())),
            HashMap::putAll);
  }

  private BooleanExpression isVisibleTo(UUID userId) {
    BooleanExpression isPublic = qChannel.type.eq(ChannelType.PUBLIC);
    BooleanExpression isJoinedPrivate = JPAExpressions
        .selectOne()
        .from(qReadStatus)
        .where(qReadStatus.channel.eq(qChannel)
            .and(qReadStatus.user.id.eq(userId)))
        .exists();
    return isPublic.or(isJoinedPrivate);
  }

  private JPQLSubQuery<Instant> lastMessageAtSubQuery() {
    return JPAExpressions.select(qMessage.createdAt.max())
        .from(qMessage)
        .where(qMessage.channel.id.eq(qChannel.id));
  }

}
