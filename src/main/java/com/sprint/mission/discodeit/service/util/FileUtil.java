package com.sprint.mission.discodeit.service.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class FileUtil {
    // 저장할 경로의 파일 초기화
    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("[폴더 생성 실패]" + e);
            }
        }
    }

    // 파일 데이터 초기화
    public static void clearDirectory(Path directory) {
        if (Files.exists(directory)) {
            try (Stream<Path> stream = Files.list(directory)) {
                stream.forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 객체 직렬화
    public static <T> void save(Path filePath, T data) {
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 객체 역직렬화
    public static <T> T loadSingle(Path filePath) {
        if (!Files.exists(filePath)) {
            throw new NoSuchElementException("[조회 실패] 해당 경로에 파일이 존재하지 않습니다: " + filePath);
        }

        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[파일 읽기 오류] 데이터를 복원할 수 없습니다: " + filePath, e);
        }
    }

    // 객체 역직렬화 (리스트)
    public static <T> List<T> load(Path directory) {
        if (!Files.exists(directory)) {
            throw new NoSuchElementException("[조회 실패] 데이터 디렉토리가 존재하지 않습니다: " + directory);
        }

        try {
            List<T> list = Files.list(directory)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (T) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("[파일 손상] 데이터를 읽을 수 없는 파일이 있습니다: " + path, e);
                        }
                    })
                    .toList();
            return list;
        } catch (IOException e) {
            throw new RuntimeException("[시스템 오류] 파일 목록을 가져오는 중 문제가 발생했습니다.", e);
        }
    }
}
