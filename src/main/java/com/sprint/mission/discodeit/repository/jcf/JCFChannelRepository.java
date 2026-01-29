//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//
//import java.util.*;
//
//public class JCFChannelRepository implements ChannelRepository {
//    private final Map<UUID, Channel> data;
//
//    public JCFChannelRepository() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public void save(Channel channel) {
//        data.put(channel.getId(), channel);
//    }
//
//    @Override
//    public void delete(UUID channelId) {
//        this.data.remove(channelId);
//    }
//
//    @Override
//    public List<Channel> loadAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//}
