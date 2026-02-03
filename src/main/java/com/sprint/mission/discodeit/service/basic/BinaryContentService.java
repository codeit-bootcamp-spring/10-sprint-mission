package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentResponseDto create(BinaryContentRequestDto binaryContentCreateDto){
        BinaryContent binaryContent = new BinaryContent(
                binaryContentCreateDto.userId(),
                binaryContentCreateDto.data(),
                binaryContentCreateDto.contentType(),
                binaryContentCreateDto.fileName());

        binaryContentRepository.save(binaryContent);
        return new BinaryContentResponseDto(
                binaryContent.getCreatedAt(),
                binaryContent.getUserId(),
                binaryContent.getData(),
                binaryContent.getContentType(),
                binaryContent.getFileName()
        );
    }

    public BinaryContentResponseDto find(UUID binaryContentId){
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent 없음"));

        return new BinaryContentResponseDto(
                binaryContent.getCreatedAt(),
                binaryContent.getUserId(),
                binaryContent.getData(),
                binaryContent.getContentType(),
                binaryContent.getFileName()
        );
    }

    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryContentIds){

        return binaryContentIds.stream()
                .map(ids -> binaryContentRepository.findById(ids).orElseThrow(()-> new NoSuchElementException("BinaryContent가 없습니다.")))
                .map(binaryContent -> new BinaryContentResponseDto(
                        binaryContent.getCreatedAt(),
                        binaryContent.getUserId(),
                        binaryContent.getData(),
                        binaryContent.getContentType(),
                        binaryContent.getFileName()
                )).toList();

    }

    public void delete(UUID binaryContentId){
        binaryContentRepository.deleteById(binaryContentId);
    }


}
