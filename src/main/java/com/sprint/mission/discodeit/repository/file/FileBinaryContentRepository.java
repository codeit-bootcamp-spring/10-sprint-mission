package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements BinaryContentRepository {


    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directoryPath) {
        super(directoryPath + File.separator + "BinaryContent.ser");
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> idList) {
        Map<UUID, BinaryContent> data = load();
        return data.values().stream()
                .filter(bc -> idList.contains(bc.getId()))
                .toList();
    }

    @Override
    public void deleteByIds(List<UUID> idList) {
        Map<UUID, BinaryContent> data = load();
        data.values().removeIf(bc -> idList.contains(bc.getId()));
        writeToFile(data);
    }

}
