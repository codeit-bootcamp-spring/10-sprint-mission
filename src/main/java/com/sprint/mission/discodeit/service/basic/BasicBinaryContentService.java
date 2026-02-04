package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicBinaryContentService implements BinaryContentService {
    BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContentDTO create(BinaryContentCreateDTO binaryContentCreateDTO) {
        BinaryContent binaryContent = new BinaryContent(binaryContentCreateDTO.fileName(), binaryContentCreateDTO.fileType(), binaryContentCreateDTO.bytes());
        this.binaryContentRepository.save(binaryContent);
        return new BinaryContentDTO(binaryContent.getFileName(), binaryContent.getFileType(), binaryContent.getBytes());
    }

    @Override
    public BinaryContentDTO find(UUID binaryContentId) {
        BinaryContent binaryContent = this.binaryContentRepository.loadById(binaryContentId);
        return new BinaryContentDTO(binaryContent.getFileName(), binaryContent.getFileType(), binaryContent.getBytes());
    }

    @Override
    public List<BinaryContentDTO> findAllByIdIn(List<UUID> binaryContentIdList) {
        List<BinaryContentDTO> binaryContentDTOList = new ArrayList<>();

        for (UUID binaryContentId : binaryContentIdList) {
            BinaryContent binaryContent = this.binaryContentRepository.loadById(binaryContentId);
            BinaryContentDTO binaryContentDTO = new BinaryContentDTO(binaryContent.getFileName(),
                    binaryContent.getFileType(),
                    binaryContent.getBytes());
            binaryContentDTOList.add(binaryContentDTO);
        }

        return binaryContentDTOList;
    }

    @Override
    public void delete(UUID binaryContentId) {
        this.binaryContentRepository.delete(binaryContentId);
    }
}
