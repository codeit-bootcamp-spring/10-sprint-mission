package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public JCFBinaryContentRepository(UserRepository userRepository, MessageRepository messageRepository) {
        this.data = new HashMap<>();
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent bc = data.get(id);
            if (bc != null) result.add(bc);
        }
        return result;
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return Optional.empty();

        UUID binaryContentId = userOpt.get().getBinaryContentId();
        if (binaryContentId == null) return Optional.empty();

        return findById(binaryContentId);
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        if (messageId == null) return List.of();

        Optional<Message> msgOpt = messageRepository.findById(messageId);
        if (msgOpt.isEmpty()) return List.of();

        List<UUID> ids = msgOpt.get().getBinaryContentIds();
        if (ids == null || ids.isEmpty()) return List.of();

        return findAllByIdIn(ids);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
