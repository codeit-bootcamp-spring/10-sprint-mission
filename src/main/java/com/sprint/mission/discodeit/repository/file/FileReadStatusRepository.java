package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(){
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),"file-data-map", ReadStatus.class.getSimpleName());
        if(Files.notExists(DIRECTORY)){
            try{
                Files.createDirectories(DIRECTORY);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) { return DIRECTORY.resolve(id + EXTENSION); }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try(
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))
        )
        {
            oos.writeObject(readStatus);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public void deleteByID(UUID id) {
        Path path = resolvePath(id);
        try{
            Files.delete(path);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ReadStatus> findByID(UUID id) {
        ReadStatus readStatus = null;
        Path path = resolvePath(id);
        if(Files.exists(path)){
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))){
                readStatus = (ReadStatus) ois.readObject();
            } catch(IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(readStatus);
    }

    @Override
    public List<ReadStatus> findAll() {
        try{
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(p -> {
                        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))){
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
