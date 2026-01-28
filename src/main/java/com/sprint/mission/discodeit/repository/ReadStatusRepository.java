package com.sprint.mission.discodeit.repository;

    import com.sprint.mission.discodeit.entity.ReadStatus;
    import java.util.Optional;
    import java.util.List;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    Optional<ReadStatus> findById(String id);
    List<ReadStatus> findByUserId(String userId);
    void deleteById(String id);
}
