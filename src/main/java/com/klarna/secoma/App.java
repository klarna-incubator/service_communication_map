package com.klarna.secoma;

import static com.klarna.secoma.LogParser.parse;
import static com.klarna.secoma.LogReader.streamLogs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		Path workDir = Paths.get(args[0]);

		Stream<LogEntry> logs = parse(streamLogs(workDir));

		logs.forEach(log -> System.out.println(log));
		SpringApplication.run(App.class, args);
	}
}
