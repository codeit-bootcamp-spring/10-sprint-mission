package com.sprint.mission.discodeit.binarycontent.repository;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JPABinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

}
