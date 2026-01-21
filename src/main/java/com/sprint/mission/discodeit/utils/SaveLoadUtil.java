package com.sprint.mission.discodeit.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadUtil {
    public static <T extends Serializable> List<T> load(String path){
        File file = new File(path); // path(channel.dat) 경로의 파일 참조하는 File 객체.

        // 파일이 존재하지 않으면 텅 빈 리스트를 리턴.
        if(!file.exists()){
            return new ArrayList<T>();
        }

        // FileInputStream으로 파일을 열고 BufferedInputStream으로 읽기 성능을 향상시킴
        // ObjectInputStream으로 직렬화된 객체를 역직렬화(바이트 → 객체)하여 읽음.
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))){
            Object object = ois.readObject(); // 파일에 저장된 바이트 데이터를 역직렬화하여 객체 형태로 읽어온 뒤 object에 대입.
            return (List<T>) object; // 읽어온 객체를 (List<T>) 형으로 캐스팅 후 리턴.
        } catch (EOFException e) {
            return new ArrayList<T>();
        } catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("파일을 읽어올 수 없습니다." + path, e);
        }
    }

    public static <T extends Serializable> void save(List<T> data, String path){
        // FileOutputStream을 통해 파일에 아웃풋 스트림을 얹는다.
        // BufferedOutputStream을 FileOutputStream 위에 얹어서 쓰기 효율을 증가시킨다.
        // ObjectOutputStream을 BufferedOutputStream 위에 얹어서 객체를 직렬화하여 파일에 기록한다.
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
            oos.writeObject(data); // data 객체를 직렬화하여 파일에 저장한다.
        } catch (IOException e) {
            throw new IllegalStateException("파일이 존재하지 않거나 유효하지 않은 입력입니다.");
        }
    }


}
