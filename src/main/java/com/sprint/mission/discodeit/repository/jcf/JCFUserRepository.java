package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

@Repository
@ConditionalOnProperty(value = "discodeit.repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {
	private static UserRepository instance;

	private final List<User> data;

	private JCFUserRepository() {
		this.data = new ArrayList<>();
	}

	public static UserRepository getInstance() {
		if (instance == null)
			instance = new JCFUserRepository();
		return instance;
	}

	@Override
	public User save(User user) {
		// 이미 해당 id를 가진 객체가 존재한다면 갱신
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId().equals(user.getId())) {
				data.set(i, user);
				return user;
			}
		}

		data.add(user);
		return user;
	}

	@Override
	public Optional<User> findById(UUID id) {
		return data.stream()
			.filter(user -> user.getId().equals(id))
			.findFirst();
	}

	@Override
	public Optional<User> findByUserName(String userName) {
		return data.stream()
			.filter(user -> user.getUserName().equals(userName))
			.findFirst();
	}

	@Override
	public List<User> findAll() {
		return data;
	}

	@Override
	public void delete(UUID id) {
		if (!data.removeIf(user -> user.getId().equals(id)))
			throw new NoSuchElementException("id가 " + id + "인 유저는 존재하지 않습니다.");
	}
}
