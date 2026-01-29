package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    @Override
    public void delete(UUID id) {

    }
}
