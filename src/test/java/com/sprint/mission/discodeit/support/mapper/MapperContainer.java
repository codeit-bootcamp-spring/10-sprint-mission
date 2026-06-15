package com.sprint.mission.discodeit.support.mapper;

import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapper;
import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapperImpl;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentMapperImpl;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelMapperImpl;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageMapperImpl;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationMapper;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationMapperImpl;
import com.sprint.mission.discodeit.readstatus.presentation.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.readstatus.presentation.mapper.ReadStatusMapperImpl;
import com.sprint.mission.discodeit.user.presentation.mapper.UserMapper;
import com.sprint.mission.discodeit.user.presentation.mapper.UserMapperImpl;
import java.util.HashMap;
import java.util.Map;

public final class MapperContainer {

  private static final Map<Class<?>, Object> container = new HashMap<>();

  static {
    BinaryContentMapper binaryContentMapper = new BinaryContentMapperImpl();
    container.put(BinaryContentMapper.class, binaryContentMapper);
    UserMapper userMapper = new UserMapperImpl();
    container.put(UserMapper.class, userMapper);
    ChannelMapper channelMapper = new ChannelMapperImpl();
    container.put(ChannelMapper.class, channelMapper);
    MessageMapper messageMapper = new MessageMapperImpl();
    container.put(MessageMapper.class, messageMapper);
    ReadStatusMapper readStatusMapper = new ReadStatusMapperImpl();
    container.put(ReadStatusMapper.class, readStatusMapper);
    AuthMapper authMapper = new AuthMapperImpl();
    container.put(AuthMapper.class, authMapper);
    NotificationMapper notificationMapper = new NotificationMapperImpl();
    container.put(NotificationMapper.class, notificationMapper);
  }

  private MapperContainer() {
  }

  public static <T> T get(Class<T> type) {
    return type.cast(container.get(type));
  }
}
