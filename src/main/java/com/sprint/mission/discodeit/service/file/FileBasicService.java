package com.sprint.mission.discodeit.service.file;

import java.io.*;
import java.util.*;

public class FileBasicService<T extends Serializable> {
    private String filePath;
    private final Map<UUID, T> data;

    public FileBasicService(String fileName){
        this.filePath = "./"+fileName+".ser";
        this.data = loadFromFile();
    }

    public void put(UUID id, T value){
        data.put(id, value);
        saveFile(data);
    }
    public T get(UUID id){
        return data.get(id);
    }
    public Collection<T> values(){
        return data.values();
    }
    public void remove(UUID id){
        data.remove(id);
        saveFile(data);
    }
    public boolean containsKey(UUID id){
        return data.containsKey(id);
    }
    private void saveFile(Map<UUID, T> data){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))){
            oos.writeObject(data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private Map<UUID, T> readFile(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))){
            Map<UUID, T> result = new HashMap<>();
            result = (Map<UUID,T>) ois.readObject();
            if(result == null){
                throw new NoSuchElementException("데이터 없음");
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }
     private Map<UUID, T> loadFromFile(){
        File file = new File(filePath);
        if(!file.exists()){
            return new HashMap<>();
        }
        return readFile();
     }
}
