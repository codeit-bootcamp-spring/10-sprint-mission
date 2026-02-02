package com.sprint.mission.discodeit.repository.file;

import static com.sprint.mission.discodeit.util.FilePath.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
public class FileChannelRepository implements ChannelRepository {
	private final FileIo<Channel> channelFileIo;
	private final FileIo<User> userFileIo;

	private FileChannelRepository() {
		channelFileIo = new FileIo<>(CHANNEL_DIRECTORY);
		this.channelFileIo.init();
		userFileIo = new FileIo<>(USER_DIRECTORY);
		this.userFileIo.init();
	}

	@Override
	public Channel save(Channel channel) {
		channelFileIo.save(channel.getId(), channel);
		return channel;
	}

	@Override
	public Optional<Channel> findById(UUID channelId) {
		return channelFileIo.load().stream()
			.filter(ch -> ch.getId().equals(channelId))
			.findFirst();
	}

	@Override
	public List<Channel> findAll() {
		return channelFileIo.load();
	}

	@Override
	public void delete(UUID channelId) {
		channelFileIo.delete(channelId);
	}
}
