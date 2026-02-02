package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

	private final FileIo<Message> messageFileIo;
	private final FileIo<User> userFileIo;
	private final FileIo<Channel> channelFileIo;

	private FileMessageRepository(@Value("${discodeit.repository.file-directory}") String directory) {
		this.messageFileIo = new FileIo<>(Paths.get(directory + Message.class.getSimpleName().toLowerCase()));
		messageFileIo.init();
		this.userFileIo = new FileIo<>(Paths.get(directory + User.class.getSimpleName().toLowerCase()));
		userFileIo.init();
		this.channelFileIo = new FileIo<>(Paths.get(directory + Channel.class.getSimpleName().toLowerCase()));
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
