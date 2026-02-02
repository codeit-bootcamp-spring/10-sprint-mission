package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> binaryContentList;

    public JCFBinaryContentRepository() {
     binaryContentList = new ArrayList<>();
    }

    @Override
    public Optional<BinaryContent> find(UUID contentID) {
        return binaryContentList.stream()
                .filter(bc -> bc.getId().equals(contentID))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(binaryContentList);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContentList.removeIf(bc -> bc.getId().equals(binaryContent.getId()));
        binaryContentList.add(binaryContent);
        return binaryContent;
    }

    @Override
    public void delete(UUID contentID) {
        binaryContentList.removeIf(bc -> bc.getId().equals(contentID));
    }
}
