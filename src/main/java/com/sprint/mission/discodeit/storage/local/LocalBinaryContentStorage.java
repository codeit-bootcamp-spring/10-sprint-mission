package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileReadFailException;
import com.sprint.mission.discodeit.exception.file.FileSaveFailException;
import com.sprint.mission.discodeit.exception.others.StorageInitException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }


    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new StorageInitException(root,"Could not initialize storage root");
        }
    }



    @Override
    public UUID put(UUID id, byte[] bytes) {
        try{
            Files.write(resolvePath(id),bytes);
            return id;
        } catch (IOException e) {
            throw new FileSaveFailException(id);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try{
            return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new FileReadFailException(id);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        try {
            InputStream is = get(dto.getId());
            InputStreamResource resource = new InputStreamResource(is);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + dto.getFileName() + "\"");
            headers.setContentType(MediaType.parseMediaType(dto.getContentType()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(dto.getSize())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 파일 경로 설정(./경로/UUID)
    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
