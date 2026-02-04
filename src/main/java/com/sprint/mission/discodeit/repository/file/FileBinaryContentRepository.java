package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("binaryContentRepository")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private List<BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화
    public void serialize(List<BinaryContent> binaryContents) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("binaryContentsList.ser"))) {
            oos.writeObject(binaryContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<BinaryContent> deserialize() {
        File file = new File("binaryContentsList.ser");
        List<BinaryContent> binaryContents = new ArrayList<>();

        if (!file.exists()) {
            return binaryContents;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            binaryContents = (List<BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return binaryContents;
    }

    @Override
    public void save(BinaryContent binaryContent) {
        this.data.add(binaryContent);
        serialize(this.data);
    }

    @Override
    public void delete(UUID binaryContentId) {
        this.data = deserialize();
        for (BinaryContent binaryContent : this.data) {
            if (binaryContent.getId().equals(binaryContentId)) {
                this.data.remove(binaryContent);
                serialize(this.data);
                break;
            }
        }
    }

    @Override
    public List<BinaryContent> loadAll() {
        return deserialize();
    }

    @Override
    public BinaryContent loadById(UUID binaryContentId) {
        this.data = deserialize();
        for(BinaryContent binaryContent : this.data) {
            if (binaryContent.getId().equals(binaryContentId)) {
                return binaryContent;
            }
        }
        throw new NoSuchElementException();
    }
}
