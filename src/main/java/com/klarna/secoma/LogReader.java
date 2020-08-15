package com.klarna.secoma;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public final class LogReader {

	public static Stream<String> streamLogs(Path folder) {
		List<String> lines = new LinkedList<>();
		readDataFolder(folder.resolve(folder), lines);
		return lines.stream();
	}

	private static void readDataFolder(Path path, List<String> data) {
		System.out.println("\n = " + path.toAbsolutePath());
		timed(() -> readAllFiles(path, data), "total");
	}

	private static void readAllFiles(Path dataFolder, List<String> allLines) {
		try (Stream<Path> paths = Files.walk(dataFolder)) {
			paths	.parallel()
					.filter(path -> path.toFile()
										.isFile())
					.forEach(path -> timed(() -> readFileIntoList(path, allLines), "  - " + path.getFileName()));
		} catch (IOException ioe) {
			throw new RuntimeException("Cannot read " + dataFolder, ioe);
		}
	}

	private static void readFileIntoList(Path source, List<String> allLines) {
		try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				allLines.add(line);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Cannot read " + source, ex);
		}
	}

	private static void timed(Runnable runnable, String msg) {
		Instant start = Instant.now();
		runnable.run();
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		System.out.println(format("%s %sd %sh %sm %ss %sms %sns", msg, duration.toDaysPart(), duration.toHoursPart(),
				duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart(), duration.toNanosPart()));
	}

	private LogReader() {
	}
}
