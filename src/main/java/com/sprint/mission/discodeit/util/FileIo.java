package com.sprint.mission.discodeit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 도메인마다 디렉토리를 구분하고 각 객체는 하나의 파일로 저장한다.
 * 파일의 이름은 객체의 id 필드값으로 한다.
 */
public class FileIo<T> {
	private final Path directory;

	public FileIo(Path directory) {
		this.directory = directory;
	}

	public void init() {
		// 저장할 경로의 파일 초기화
		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// 디렉토리 안에서 순회를 하며 파일을 찾는다.
	public List<T> load() {
		if (Files.exists(directory)) {
			try {
				List<T> list = Files.list(directory)
					.map(path -> {
						try (
							FileInputStream fis = new FileInputStream(path.toFile());
							ObjectInputStream ois = new ObjectInputStream(fis)
						) {
							Object data = ois.readObject();
							return (T)data;
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					})
					.collect(Collectors.toList()); // toList()는 unmodifiable이라서 수정이 안 됨
				return list;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			return new ArrayList<>();
		}
	}

	// 객체의 id를 파일명으로 하여 저장한다.
	public T save(UUID id, T data) {
		try {
			// 디렉토리가 존재하지 않으면 생성하도록 한다.
			Files.createDirectories(directory);

			try (
				FileOutputStream fos = new FileOutputStream(directory.resolve(id.toString().concat(".ser")).toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos)
			) {
				oos.writeObject(data);
				return data;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param id: 객체의 id 필드
	 */
	public void delete(UUID id) {
		File deleteFile = new File(
			directory.resolve(id.toString().concat(".ser")).toString()
		);

		if (deleteFile.exists()) {
			if (!deleteFile.delete()) {
				throw new RuntimeException("id가 " + id + "인 객체를 삭제할 수 없습니다.");
			}
		} else {
			throw new NoSuchElementException("id가 " + id + "인 객체가 존재하지 않습니다.");
		}
	}
}
