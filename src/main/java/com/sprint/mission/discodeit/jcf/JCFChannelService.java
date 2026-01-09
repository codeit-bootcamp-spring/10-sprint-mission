//package com.sprint.mission.discodeit.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private final Map<UUID, Channel> data;
//
//    public JCFChannelService() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public Channel create(Channel channel) {
//        data.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public Channel read(UUID id) {
//        return data.get(id);
//    }
//
//    @Override
//    public List<Channel> readAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    @Override
//    public Channel update(Channel channel) {
//        data.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public void delete(UUID id) {
//        data.remove(id);
//    }
//}
