package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ReadStatusRepositoryImpl implements ReadStatusRepository{
    @Override
    public ReadStatus save(ReadStatus readStatus) {
        return null;
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return null;
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.of();
    }

    @Override
    public void delete(ReadStatus readStatus) {

    }
}
