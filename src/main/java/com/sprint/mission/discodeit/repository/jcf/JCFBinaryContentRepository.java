package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFBinaryContentRepository extends JCFDomainRepository<BinaryContent> implements BinaryContentRepository {

    public JCFBinaryContentRepository() {
        super(new HashMap<>());
    }

    @Override
    public BinaryContent save(BinaryContent entity) {
        getData().put(entity.getId(), entity);
        return entity;
    }
}
