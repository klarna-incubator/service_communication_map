package com.klarna.secoma.dataimporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class LogReader {
	private static List<String> EXTS = List.of(".csv", ".log", ".txt");

	public static Stream<String> streamLogs(Path folder) {
		return streamAllFiles(folder);
	}

	private static Stream<String> streamAllFiles(Path dataFolder) {
		try {
			return Files.walk(dataFolder).parallel().filter(path -> isLogFile(path)).flatMap(LogReader::streamFile);
		} catch (IOException ioe) {
			System.err.println(
					"Cannot read folder " + dataFolder.toAbsolutePath() + " due to " + ioe.getCause().getMessage());
			ioe.printStackTrace();
		}
		return Stream.empty();
	}

	private static Stream<String> streamFile(Path path) {
		try {
			return Files.lines(path);
		} catch (IOException ioe) {
			System.err.println("Cannot read file " + path.toAbsolutePath() + " due to " + ioe.getCause().getMessage());
			ioe.printStackTrace();
		}
		return Stream.empty();
	}

	private static boolean isLogFile(Path path) {
		File file = path.toFile();
		String name = file.getName();
		return file.isFile() && EXTS.stream().anyMatch(ext -> name.endsWith(ext));
	}

	private LogReader() {
	}
}
