package com.sprint.mission.discodeit.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SerializedFileUtils {

    private SerializedFileUtils() {
    }

    public static Object deserialize(Path filePath, String entityName) {
        // 파일 존재 여부 확인
        if (!Files.exists(filePath)) {
            throw new RuntimeException(entityName + " 파일이 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // 파일 역직렬화
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(entityName + " 파일 로딩을 실패했습니다.");
        }
    }

    public static List<Object> deserializeAll(Path dir, String filePrefix, String entityName) {
        List<Object> data = new ArrayList<>();

        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(path -> path.getFileName().toString().startsWith(filePrefix + "-")) // 파일명이 filePrefix로 시작해야 함
                    .filter(path -> path.getFileName().toString().endsWith(".ser")) // 파일의 확장자가 ".ser"이어야 함
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {

                            // 파일 조회 후 컬렉션에 저장
                            Object object = ois.readObject();
                            data.add(object);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(entityName + " 파일 로딩을 실패했습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(entityName + " 디렉토리 조회를 실패했습니다.");
        }

        // 컬렉션 반환
        return data;
    }

    public static void deleteFileOrThrow(Path filePath, String entityName) {
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new RuntimeException(entityName + " 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(entityName + " 파일 삭제를 실패했습니다.");
        }
    }
}
