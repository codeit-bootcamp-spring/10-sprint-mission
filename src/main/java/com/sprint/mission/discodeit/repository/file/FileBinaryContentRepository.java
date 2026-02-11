package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private List<BinaryContent> binaryContentList;

    private final Path STORE_FILE;

    public FileBinaryContentRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        Path BASE_PATH = Path.of(directoryPath).resolve("binaryContent");

        this.STORE_FILE = BASE_PATH.resolve("binaryContent.ser");

        init(BASE_PATH);
        loadData();
    }

    private void init(Path BASE_PATH) {
        try {
            if(!Files.exists(BASE_PATH)) {
                Files.createDirectories(BASE_PATH);
            }
        } catch(Exception e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    private void loadData() {
        if(!Files.exists(STORE_FILE)) {
            binaryContentList = List.of();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE.toFile()))) {
            binaryContentList = (List<BinaryContent>) ois.readObject();
        }catch(Exception e) {
            System.out.println("Data load failed." + e.getMessage());
        }
    }

    private void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE.toFile()))) {
            oos.writeObject(binaryContentList);
        }catch(Exception e) {
            System.out.println("Data save failed." + e.getMessage());
        }
    }

    @Override
    public Optional<BinaryContent> find(UUID contentID) {
        loadData();
        return binaryContentList.stream()
                .filter(binaryContent -> binaryContent.getId().equals(contentID))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        loadData();
        return new ArrayList<>(binaryContentList);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        loadData();
        for(int i=0; i<binaryContentList.size(); i++){
            if(binaryContentList.get(i).getId().equals(binaryContent.getId())){
                binaryContentList.set(i, binaryContent);
                saveData();
                return binaryContent;
            }
        }
        binaryContentList.add(binaryContent);
        saveData();
        return binaryContent;
    }

    @Override
    public void delete(UUID contentID) {
        loadData();
        binaryContentList.removeIf(binaryContent -> binaryContent.getId().equals(contentID));
        saveData();
    }
}
