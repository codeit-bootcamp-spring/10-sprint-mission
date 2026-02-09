package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
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
