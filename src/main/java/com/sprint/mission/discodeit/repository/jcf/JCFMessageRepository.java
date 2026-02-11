package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

@Repository
@ConditionalOnProperty(value = "discodeit.repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {
	private static MessageRepository instance;
	private final List<Message> data;

	private JCFMessageRepository() {
		this.data = new ArrayList<>();
	}

	public static MessageRepository getInstance() {
		if (instance == null)
			instance = new JCFMessageRepository();
		return instance;
	}

	@Override
	public Message save(Message message) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId().equals(message.getId())) {
				data.set(i, message);
				return message;
			}
		}

		data.add(message);
		return message;
	}

	@Override
	public Optional<Message> findById(UUID id) {
		return data.stream()
			.filter(message -> message.getId().equals(id))
			.findFirst();
	}

	@Override
	public void delete(UUID id) {
		if (!data.removeIf(message -> message.getId().equals(id)))
			throw new NoSuchElementException("id가 " + id + "인 메시지는 존재하지 않습니다.");
	}
}
