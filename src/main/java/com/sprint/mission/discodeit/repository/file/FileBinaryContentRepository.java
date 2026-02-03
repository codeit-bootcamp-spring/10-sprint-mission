package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(){
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),"file-data-map",BinaryContent.class.getSimpleName());
        if(Files.notExists(DIRECTORY)){
            try{
                Files.createDirectories(DIRECTORY);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id){
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return binaryContent;
    }

    @Override
    public void deleteByID(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BinaryContent> findbyId(UUID id) {
        BinaryContent nullable = null;
        Path path = resolvePath(id);
        if(Files.exists(path)){
            try(
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))
            )
            {
                nullable = (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(nullable);
    }

    @Override
    public List<BinaryContent> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(p -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
