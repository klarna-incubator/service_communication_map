package com.klarna.secoma;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public final class LogParser {
	private static final String NOTHING = "";

	private static final String DQ = "\"";

	private static final String _RAW = "\"_raw\"";

	private static final String _TIME = "\"_time\"";

	private static final String SOURCETYPE = "sourcetype";

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	/**
	 * assume incoming stream of CSV rows schema to be
	 * [{@code sourcetype, "_time", "_raw"}]
	 */
	public static Stream<LogEntry> parse(Stream<String> rows) {
		return rows	.map(line -> {

						String[] data = line.split(",", 3);
						String name = data[0];
						String time = data[1];
						String raw = data[2];

						if (SOURCETYPE.equals(name) && _TIME.equals(time) && _RAW.equals(raw)) {
							System.out.println("skip header" + line);
							return null;
						}

						try {
							Instant instant = Instant.from(dtf.parse(time.replace(DQ, NOTHING)));
							return (LogEntry) new SimpleLogEntry(name.replace(DQ, NOTHING), instant, UUID.randomUUID());//TODO extract ID
						} catch (DateTimeParseException dtpe) {
							throw new RuntimeException("can't parse " + time, dtpe);
						}
					})
					.filter(Objects::nonNull);
	}

	private LogParser() {
	}

}
