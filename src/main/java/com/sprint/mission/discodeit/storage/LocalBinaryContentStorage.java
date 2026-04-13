package com.sprint.mission.discodeit.storage;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type",havingValue = "local") //discodeit.storage.type값이 local 일때만 bean으로 등록.
public class LocalBinaryContentStorage implements BinaryContentStorage{

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath); //문자열 경로를 Path객체로 변환
    }

    @PostConstruct
    public void init(){
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }

    }

    //파일의 실제 저장위치에 대한 규칙을 정의합니다.
    //{root}/{UUID}
    private Path resolvePath(UUID id){
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {

        Path path = resolvePath(id);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {

        InputStream stream = get(dto.id());

        Resource resource = new InputStreamResource(stream); //Spring은 HTTP 응답 body에 파일을 보낼 때 Resoucre 타입을 사용한다.

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.fileName() + "\"" //브라우저에게 다운로드 파일이다.
                )
                .contentType(MediaType.parseMediaType(dto.contentType()))// 파일 타입
                .body(resource);


    }

}
