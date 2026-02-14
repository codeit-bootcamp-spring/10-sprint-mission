    package com.sprint.mission.discodeit.repository.file;

    import com.sprint.mission.discodeit.entity.Channel;
    import com.sprint.mission.discodeit.repository.ChannelRepository;

    import java.io.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    public class FileChannelRepository implements ChannelRepository {
        private final String FILE_PATH = "channels.dat";

        @Override
        public Channel save(Channel channel) {
            List<Channel> channels = findAll();
            boolean found = false;

            for (int i = 0; i < channels.size(); i++) {
                if (channels.get(i).getId().equals(channel.getId())) {
                    channels.set(i, channel);
                    found = true;
                    break;
                }
            }

            if (!found) channels.add(channel);

            saveToFile(channels);
            return channel;
        }

        @Override
        public Optional<Channel> findById(UUID id) {
            return findAll().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Channel> findAll() {
            File file = new File(FILE_PATH);
            if (!file.exists()) return new ArrayList<>();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<Channel>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return new ArrayList<>();
            }
        }

        @Override
        public void deleteById(UUID id) {
            List<Channel> channels = findAll();
            channels.removeIf(c -> c.getId().equals(id));
            saveToFile(channels);
        }

        private void saveToFile(List<Channel> channels) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
               oos.writeObject(channels);
            } catch (IOException e) {
                throw new RuntimeException("채널 파일 저장 실패", e);
            }
        }
    }
