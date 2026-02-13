package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BinaryContentResponseDto create(BinaryContentRequestDto binaryContentCreateDto){
        if(binaryContentCreateDto.userId() != null && !userRepository.existsById(binaryContentCreateDto.userId())){//userId가 있을경우
            userRepository.findById(binaryContentCreateDto.userId())
                    .orElseThrow(() -> new NoSuchElementException("존재하는 유저가 없습니다."));
        }
        if(binaryContentCreateDto.messageId() != null && !messageRepository.existsById(binaryContentCreateDto.messageId())){
            messageRepository.findById(binaryContentCreateDto.messageId())
                    .orElseThrow(() -> new NoSuchElementException("존재하는 메시지가 없습니다."));
        }
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
        if(!binaryContentRepository.existsById(binaryContentId)){
            throw new NoSuchElementException("삭제할 BinaryContent가 없습니다.");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

}
