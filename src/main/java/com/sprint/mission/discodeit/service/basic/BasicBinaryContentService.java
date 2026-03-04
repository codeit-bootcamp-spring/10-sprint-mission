package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    @Value("${discodeit.upload.attachment}")
    private String ATTACHMENT_DIR;

    @Override
    public BinaryContentDto.binaryContentResponse create(BinaryContentDto.binaryContentCreateRequest createReq) {
        BinaryContent binaryContent = new BinaryContent(createReq.contentType(), createReq.filename(), ATTACHMENT_DIR);
        // 첨부파일 저장
        Path attachmentDir = Paths.get(ATTACHMENT_DIR);
        String fileName = binaryContent.getId() + "." + StringUtils.getFilenameExtension(createReq.filename());
        try {
            Files.createDirectories(attachmentDir);
            Files.write(attachmentDir.resolve(fileName), createReq.bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binaryContentRepository.save(binaryContent);
        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentDto.binaryContentResponse findById(UUID uuid) {
        return binaryContentRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));
    }

    @Override
    public List<BinaryContentDto.binaryContentResponse> findAllByIdIn(List<UUID> uuids) {
        return binaryContentRepository.findAllByIdIn(uuids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        binaryContentRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ATTACHMENT_DIR), uuid + ".*")) {
            for (Path p : stream) {
                Files.deleteIfExists(p);
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binaryContentRepository.deleteById(uuid);
    }

    private BinaryContentDto.binaryContentResponse toResponse(BinaryContent binaryContent) {
        byte[] bytes = null;
        String fileName = binaryContent.getId() + "." + StringUtils.getFilenameExtension(binaryContent.getFileName());
        try {
            bytes = Files.readAllBytes(Paths.get(binaryContent.getUrl(), fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BinaryContentDto.binaryContentResponse(binaryContent.getId(), binaryContent.getCreatedAt(),
                binaryContent.getFileName(), bytes.length,
                binaryContent.getContentType().getMimeType(), bytes);
    }
}
