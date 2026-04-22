package com.sprint.mission.discodeit.mapper.factory;

import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.BinaryContentMapperImpl;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapperImpl;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.MessageMapperImpl;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapperImpl;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserMapperImpl;
import java.util.HashMap;
import java.util.Map;

public final class MapperContainer {

  private static final Map<Class<?>, Object> container = new HashMap<>();

  static {
    BinaryContentMapper binaryContentMapper = new BinaryContentMapperImpl();
    container.put(BinaryContentMapper.class, binaryContentMapper);
    UserMapper userMapper = new UserMapperImpl(binaryContentMapper);
    container.put(UserMapper.class, userMapper);
    ChannelMapper channelMapper = new ChannelMapperImpl(userMapper);
    container.put(ChannelMapper.class, channelMapper);
    MessageMapper messageMapper = new MessageMapperImpl(binaryContentMapper, userMapper);
    container.put(MessageMapper.class, messageMapper);
    ReadStatusMapper readStatusMapper = new ReadStatusMapperImpl();
    container.put(ReadStatusMapper.class, readStatusMapper);
  }

  private MapperContainer() {
  }

  public static <T> T get(Class<T> type) {
    return type.cast(container.get(type));
  }
}
