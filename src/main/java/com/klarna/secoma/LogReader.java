package com.klarna.secoma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class LogReader {

	public static Stream<String> streamLogs(Path folder) {
		return streamAllFiles(folder);
	}

	private static Stream<String> streamAllFiles(Path dataFolder) {
		try {
			return Files.walk(dataFolder)
						.parallel()
						.filter(path -> path.toFile()
											.isFile())
						.flatMap(LogReader::streamFile);
		} catch (IOException ioe) {
			System.err.println("Cannot read folder " + dataFolder.toAbsolutePath() + " due to " + ioe	.getCause()
																										.getMessage());
			ioe.printStackTrace();
		}
		return Stream.empty();
	}

	private static Stream<String> streamFile(Path path) {
		try {
			return Files.lines(path);
		} catch (IOException ioe) {
			System.err.println("Cannot read file " + path.toAbsolutePath() + " due to " + ioe	.getCause()
																								.getMessage());
			ioe.printStackTrace();
		}
		return Stream.empty();
	}

	private LogReader() {
	}
}
