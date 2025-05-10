/*
 */
package com.klarna.secoma;

import static com.klarna.secoma.dataimporter.LogParser.parse;
import static com.klarna.secoma.dataimporter.LogReader.streamLogs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.klarna.secoma.dataimporter.LogEntry;

public class LogParserTest {

	@Test
	public void canParseSampleData() {
		List<LogEntry> logs = loadData(getFixture("single_flow.csv"));
		int logLinesCount = 39; // log lines in CSV (not header, not last empty line)
		assertThat(logs.size(),  is(equalTo(logLinesCount)));
	}

	private static List<LogEntry> loadData(String path) {
		System.err.println("loading " + path);
		Path workDir = Paths.get(path);
		Stream<LogEntry> logs = parse(streamLogs(workDir));
		return logs.collect(Collectors.toList());
	}

	private String getFixture(String... more) {
		return getResource("fixture", more);
	}	

	private String getResource(String first, String... more) {
		return this.getClass().getClassLoader().getResource(Paths.get(first, more).toString()).getPath();
	}

}
