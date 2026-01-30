package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JCFBinaryContentRepository extends BaseFileRepository<BinaryContent> implements BinaryContentRepository {
}
