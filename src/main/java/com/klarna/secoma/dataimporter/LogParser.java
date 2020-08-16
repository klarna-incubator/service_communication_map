package com.klarna.secoma.dataimporter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import static java.lang.String.format;

public final class LogParser {

	private static final String NOTHING = "";

	private static final String DQ = "\"";

	private static final String _RAW = "\"_raw\"";

	private static final String _TIME = "\"_time\"";

	private static final String SOURCETYPE = "sourcetype";

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	private static final String UUID_GROUP = "([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})";

	private static final String CORR_REX_1 = format("\"\"correlation_id\"\"\\s*:\\s*\"\"%s\"\"", UUID_GROUP);
	private static final String CORR_REX_2 = format("\"\"correlationId\"\"\\s*:\\s*\"\"%s\"\"", UUID_GROUP);
	private static final String CORR_REX_3 = format("\\\\\"\"correlation_id\\\\\"\"\\s*:\\s*\\\\\"\"%s\\\\\"\"",
			UUID_GROUP);
	private static final List<Pattern> CORR_ID_PATERNS = List.of(Pattern.compile(CORR_REX_1),
			Pattern.compile(CORR_REX_2), Pattern.compile(CORR_REX_3));

	/**
	 * assume incoming stream of CSV rows schema to be
	 * [{@code sourcetype, "_time", "_raw"}]
	 */
	public static Stream<LogEntry> parse(Stream<String> rows) {
		return rows.map(line -> {
try {
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
				String id = CORR_ID_PATERNS.stream().map(p -> p.matcher(raw)).filter(m -> m.find()).findFirst()
						.map(m -> m.group(1)).orElse(null);
				if (Objects.isNull(id) || id.isBlank() || id.isEmpty()) {
					System.err.println("Can't find correlation id matcher for " + raw);
					return null;
				}
				return (LogEntry) new SimpleLogEntry(name.replace(DQ, NOTHING), instant, UUID.fromString(id));
			} catch (DateTimeParseException dtpe) {
				throw new RuntimeException("can't parse " + time, dtpe);
			}
}catch(Exception e) {
	System.err.println(line);
	String[] split = line.split(",", 3);
	System.err.println(split);
}
return null;
		}).filter(Objects::nonNull);
	}

	private LogParser() {
	}

}
