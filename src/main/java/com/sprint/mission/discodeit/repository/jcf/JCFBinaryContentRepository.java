package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    public final Map<UUID, BinaryContent> data;

    public  JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getUserId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID contentId) {
        return Optional.ofNullable(this.data.get(contentId));
    }

    @Override
    public List<BinaryContent> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID contentId) {
        return this.data.values().stream().anyMatch(u -> u.getUserId().equals(contentId));
    }

    @Override
    public void deleteById(UUID contentId) {
        this.data.remove(contentId);

    }

//    @Override
//    public boolean existProfile(UUID userId) {
//        return this.data.values().stream().anyMatch(u -> u.getUserId().equals(userId));
//    }
//
//    @Override
//    public Optional<BinaryContent> findByUserId(UUID userId) {
//        return this.data.values().stream().filter(u -> u.getUserId().equals(userId)).findFirst();
//    }
//
//    @Override
//    public Optional<BinaryContent> findByAuthorId(UUID authorId) {
//        return this.data.values().stream().filter(u -> u.getUserId().equals(authorId)).findFirst();
//    }
}
