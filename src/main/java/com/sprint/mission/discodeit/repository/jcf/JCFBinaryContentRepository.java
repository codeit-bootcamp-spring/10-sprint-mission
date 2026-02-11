package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID profileId) {
        return Optional.ofNullable(data.get(profileId));
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<byte[]> findAllImagesByIds(List<UUID> attachmentIds) {
        return idsToContents(attachmentIds).stream()
                .map(BinaryContent::getBytes)
                .collect(Collectors.toList());
    }

    private List<BinaryContent> idsToContents(List<UUID> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        data.remove(binaryContent.getId());
    }

    @Override
    public void deleteById(UUID profileId) {
        data.remove(profileId);
    }
}
