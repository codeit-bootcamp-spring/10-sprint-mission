package com.sprint.mission.discodeit.binarycontent.application.scheduler;

import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BinaryContentCleanUpScheduler {

  private final BinaryContentRepository repository;
}
