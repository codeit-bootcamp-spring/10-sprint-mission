package com.sprint.mission.discodeit.storage.s3;


import com.sprint.mission.discodeit.dto.response.FileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class S3UploadController {

    private final S3UploadService s3;
    private final S3Presigner presigner;
    private final AwsProperties props;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
        String url = s3.store(file);
        return ResponseEntity.created(URI.create(url)).build();
    }

    @GetMapping("/list")
    public List<FileResponseDto> list(
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "max", required = false, defaultValue = "100") int max
    ) {
        return s3.list(prefix, max);
    }

    @GetMapping("/download")
    public ResponseEntity<Void> download(
            @RequestParam("key") String key,
            @RequestParam(value = "filename", required = false) String filename
    ) {
        String bucket = props.getBucket();
        String name = (filename != null && !filename.isBlank())
                ? filename
                : Paths.get(key).getFileName().toString();

        // 응답 헤더(Content-Disposition)를 presign 시점에 주입
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + name + "\"")
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = presigner.presignGetObject(preReq).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }
}
