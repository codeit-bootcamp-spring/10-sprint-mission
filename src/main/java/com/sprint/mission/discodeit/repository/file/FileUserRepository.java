package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

@Repository
public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "data/users.ser";
    private final List<User> data;

    public FileUserRepository() {
        this.data = loadUsers();
    }

    private void saveUsers() {
        File file = new File(FILE_PATH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: users.ser에 저장되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<User>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 로드 중 오류 발생", e);
        }
    }

    @Override
    public User save(User user) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(user.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), user);
        } else {
            data.add(user);
        }

        saveUsers();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }


    @Override
    public void deleteById(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
        saveUsers();
    }

}



