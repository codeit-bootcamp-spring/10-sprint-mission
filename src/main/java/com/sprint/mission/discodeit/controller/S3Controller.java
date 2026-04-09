package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.exception.s3.S3Exception;
import com.sprint.mission.discodeit.service.basic.BasicS3Service;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3Controller {

    private final BasicS3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
        UUID uploadedId;
        try {
            uploadedId = s3Service.upload(file);
        } catch (IOException e) {
            throw new S3Exception();
        }
        return ResponseEntity.created(URI.create("/files/" + uploadedId)).build();
    }

    @GetMapping("/download")
    public ResponseEntity<?> download(
        @RequestParam("key") String key,
        @RequestParam(value = "filename", required = false) String filename
    ) {
        Objects.requireNonNull(key, "key must not be null");
        return s3Service.download(key, filename);
    }
}
