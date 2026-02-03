package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_NAME = "binaryContents.ser";
    private final Path filePath;
    private final List<BinaryContent> data;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String dir
    ) {
        this.filePath = Paths.get(dir,  FILE_NAME);
        this.data = loadBinaryContents();
    }

    private void saveBinaryContents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: binaryContents.ser에 저장되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("첨부파일 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<BinaryContent> loadBinaryContents() {
        if (!filePath.toFile().exists() || filePath.toFile().length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<BinaryContent>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("첨부파일 로드 중 오류 발생", e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(binaryContent.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            throw new IllegalStateException("BinaryContent는 수정할 수 없습니다.");
        } else {
            data.add(binaryContent);
        }

            saveBinaryContents();
            return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream()
                .filter(bc -> bc.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(bs -> bs.getId().equals(id));
        saveBinaryContents();
    }
}
