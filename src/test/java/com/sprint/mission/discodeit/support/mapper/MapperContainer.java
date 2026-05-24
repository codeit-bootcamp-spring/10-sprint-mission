package com.sprint.mission.discodeit.support.mapper;

import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapperImpl;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapperImpl;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.mapper.MessageMapperImpl;
import com.sprint.mission.discodeit.readstatus.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.readstatus.mapper.ReadStatusMapperImpl;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.mapper.UserMapperImpl;
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
