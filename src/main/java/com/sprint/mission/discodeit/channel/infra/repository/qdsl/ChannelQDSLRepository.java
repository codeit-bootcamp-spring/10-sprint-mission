package com.sprint.mission.discodeit.channel.infra.repository.qdsl;

import static com.sprint.mission.discodeit.binarycontent.domain.entity.QBinaryContent.binaryContent;
import static com.sprint.mission.discodeit.channel.domain.entity.QChannel.channel;
import static com.sprint.mission.discodeit.message.domain.entity.QMessage.message;
import static com.sprint.mission.discodeit.readstatus.domain.entity.QReadStatus.readStatus;
import static com.sprint.mission.discodeit.user.domain.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.domain.repository.ChannelQueryRepository;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.user.domain.entity.User;
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

@Repository
@RequiredArgsConstructor
public class ChannelQDSLRepository implements ChannelQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ChannelDetailDto> findVisibleChannelDetails(UUID userId) {
    return fetchChannelDetails(isVisibleTo(userId));
  }

  @Override
  public Optional<ChannelDetailDto> findChannelDetailById(UUID id) {
    List<ChannelDetailDto> result = fetchChannelDetails(channel.id.eq(id));
    if (result.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(result.get(0));
  }

  private List<ChannelDetailDto> fetchChannelDetails(BooleanExpression condition) {
    Map<Channel, Instant> channelsWithLastMessageAt = findChannelsWithLastMessageTime(condition);
    if (channelsWithLastMessageAt.isEmpty()) {
      return List.of();
    }
    List<UUID> channelIds = channelsWithLastMessageAt.keySet()
        .stream()
        .map(Channel::getId)
        .toList();
    Map<UUID, List<User>> channelsWithParticipants = findParticipantsGroupedByChannel(channelIds);
    Function<Channel, ChannelDetailDto> toChannelDetailResponse =
        channel -> new ChannelDetailDto(
            channel,
            channelsWithLastMessageAt.get(channel),
            channelsWithParticipants.get(channel.getId())
        );
    return channelsWithLastMessageAt.keySet().stream()
        .map(toChannelDetailResponse)
        .toList();
  }

  private Map<UUID, List<User>> findParticipantsGroupedByChannel(List<UUID> channelIds) {
    return queryFactory.select(readStatus.channel.id, user)
        .from(readStatus)
        .join(readStatus.user, user)
        .leftJoin(user.profile, binaryContent).fetchJoin()
        .where(readStatus.channel.id.in(channelIds))
        .fetch()
        .stream()
        .collect(Collectors.groupingBy(
            tuple -> tuple.get(readStatus.channel.id),
            Collectors.mapping(tuple -> tuple.get(user), Collectors.toList())
        ));
  }

  private Map<Channel, Instant> findChannelsWithLastMessageTime(BooleanExpression condition) {
    return queryFactory.select(channel, lastMessageAtSubQuery())
        .from(channel)
        .where(condition)
        .fetch()
        .stream()
        .collect(HashMap::new,
            (map, tuple)
                -> map.put(tuple.get(channel), tuple.get(lastMessageAtSubQuery())),
            HashMap::putAll);
  }

  private BooleanExpression isVisibleTo(UUID userId) {
    BooleanExpression isPublic = channel.type.eq(ChannelType.PUBLIC);
    BooleanExpression isJoinedPrivate = JPAExpressions
        .selectOne()
        .from(readStatus)
        .where(readStatus.channel.eq(channel)
            .and(readStatus.user.id.eq(userId)))
        .exists();
    return isPublic.or(isJoinedPrivate);
  }

  private JPQLSubQuery<Instant> lastMessageAtSubQuery() {
    return JPAExpressions.select(message.createdAt.max())
        .from(message)
        .where(message.channel.id.eq(channel.id));
  }

}
