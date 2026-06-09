package com.sprint.mission.discodeit.service.cache;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelCacheService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final CacheManager cacheManager;

  @Cacheable(value = "channelListCache", key = "'public'")
  public List<ChannelDto> getPublicChannelsWithoutLastMessageAtAndOnline() {
    List<ChannelDto> response = new ArrayList<>();

    Map<UUID, List<User>> userMap = new HashMap<>();
    Map<UUID, Channel> channels = new HashMap<>();

    channelRepository.findAllPublic()
        .forEach(channel -> {
          userMap.putIfAbsent(channel.getId(), new ArrayList<>());
          channels.put(channel.getId(), channel);
        });
    readStatusRepository.findAllByChannelIdInFetchUser(userMap.keySet())
        .forEach(r -> {
          userMap.get(r.getChannel().getId()).add(r.getUser());
        });

    channels.values().forEach(c -> {
      response.add(channelMapper.toDto(c, userMap.get(c.getId()),
          null, new HashSet<>()));
    });

    return response.stream()
        .sorted(Comparator.comparing(ChannelDto::createdAt))
        .toList();//채널 순서 보장
  }

  @Cacheable(value = "channelListCache", key = "#userId")
  public List<ChannelDto> getPrivateChannelsWithoutLastMessageAtAndOnline(UUID userId) {
    List<ChannelDto> response = new ArrayList<>();

    Map<UUID, List<User>> userMap = new HashMap<>();
    Map<UUID, Channel> myChannels = new HashMap<>();
    readStatusRepository.findAllPrivateByUserIdFetchChannel(userId)
        .forEach(r -> {
          userMap.putIfAbsent(r.getChannel().getId(), new ArrayList<>());
          myChannels.put(r.getChannel().getId(), r.getChannel());
        });
    readStatusRepository.findAllByChannelIdInFetchUser(userMap.keySet())
        .forEach(r -> {
          userMap.get(r.getChannel().getId()).add(r.getUser());
        });

    myChannels.values().forEach(c -> {
      response.add(channelMapper.toDto(c, userMap.get(c.getId()),
          null, new HashSet<>()));
    });

    return response.stream()
        .sorted(Comparator.comparing(ChannelDto::createdAt))
        .toList();//채널 순서 보장
  }

  public void removePrivateChannelCaches(Set<UUID> userIds) {
    Cache cache = cacheManager.getCache("channelListCache");
    for (UUID id : userIds) {
      if (cache != null) {
        cache.evict(id);
      }
    }
  }

  public void removePublicChannelCaches() {
    Cache cache = cacheManager.getCache("channelListCache");
    if (cache != null) {
      cache.evict("public");
    }
  }

  public void removeAllCaches() {
    Cache cache = cacheManager.getCache("channelListCache");
    if (cache != null) {
      cache.clear();
    }
  }
}
