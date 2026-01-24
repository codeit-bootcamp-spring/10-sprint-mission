package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static FileMessageRepository instance = null;
    public static FileMessageRepository getInstance(){
        if(instance == null){
            instance = new FileMessageRepository();
        }
        return instance;
    }
    private FileMessageRepository(){}
    public static final String FILE_PATH = "messages.dat";

    @Override
    public void fileSave(Set<Message> messages) {
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(messages);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Message> fileLoad() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashSet<>(); // 파일이 없으면 빈 셋 반환
        }

        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<Message>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fileDelete(UUID id) {
        Set<Message> messages = fileLoad();
        messages.removeIf(message -> message.getId().equals(id));
        fileSave(messages);
    }
}
