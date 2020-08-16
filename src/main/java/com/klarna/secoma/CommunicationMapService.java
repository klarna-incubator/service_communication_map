package com.klarna.secoma;

import static com.klarna.secoma.dataimporter.LogParser.parse;
import static com.klarna.secoma.dataimporter.LogReader.streamLogs;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.klarna.secoma.config.Configuration;
import com.klarna.secoma.dataimporter.LogEntry;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

@Component
public class CommunicationMapService {

	@Autowired
	Configuration config;

	public StreamResource findCommunicationMap(String correlationId) {
		UUID id = UUID.fromString(correlationId);
		return new StreamResource("foo.png", new InputStreamFactory() {
			private static final long serialVersionUID = 9031433761422193929L;

			@Override
			public InputStream createInputStream() {
				return NodeGraph.getGraphImage(loadData(config.getLogsLocation()), id);
			}
		});
	}

	private static List<LogEntry> loadData(String path) {
		Path workDir = Paths.get(path);	
		Stream<LogEntry> logs = parse(streamLogs(workDir));
		return logs.collect(Collectors.toList());
	}
}
