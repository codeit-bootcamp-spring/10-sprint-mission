package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class JCFChannelRepository extends JCFDomainRepository<Channel> implements ChannelRepository {

    public JCFChannelRepository() {
        super(new HashMap<>());
    }

    @Override
    public Channel save(Channel channel) {
        getData().put(channel.getId(), channel);
        return channel;
    }
}
