package com.klarna.secoma;

import java.util.List;
import java.util.stream.Collectors;

public class LogParser {

	public List<LogEntry> parse(List<String> logLines) {
		// assume line is CSV form of:: SERVICE_NAME, LOG

		return logLines	.stream()
						.map(line -> line.split(",", 2))
						.map(data -> new SimpleLogEntry(data))
						.collect(Collectors.toList());

	}

}
