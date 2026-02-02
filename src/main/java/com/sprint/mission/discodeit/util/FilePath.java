package com.sprint.mission.discodeit.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {
	public static final Path READ_STATUS_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "readStatuses");
	public static final Path USER_STATUS_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "userStatuses");
	public static final Path BINARY_CONTENT_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data",
		"binaryContents");
}

