package com.sprint.mission.discodeit.repository.file;

import static com.sprint.mission.discodeit.util.FilePath.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
public class FileMessageRepository implements MessageRepository {

	private final FileIo<Message> messageFileIo;
	private final FileIo<User> userFileIo;
	private final FileIo<Channel> channelFileIo;

	private FileMessageRepository() {
		this.messageFileIo = new FileIo<>(MESSAGE_DIRECTORY);
		messageFileIo.init();
		this.userFileIo = new FileIo<>(USER_DIRECTORY);
		userFileIo.init();
		this.channelFileIo = new FileIo<>(CHANNEL_DIRECTORY);
		channelFileIo.init();
	}

	@Override
	public Message save(Message newMessage) {
		return messageFileIo.save(newMessage.getId(), newMessage);
	}

	@Override
	public Optional<Message> findById(UUID id) {
		return messageFileIo.load().stream()
			.filter(u -> u.getId().equals(id))
			.findFirst();
	}

	@Override
	public void delete(UUID id) {
		try {
			messageFileIo.delete(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
