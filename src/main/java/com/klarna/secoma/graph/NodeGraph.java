package com.klarna.secoma.graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.dot.DOTExporter;

import com.klarna.secoma.dataimporter.LogEntry;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class NodeGraph {

	private static LogNode searchLogNodes(List<LogNode> nodes, String serviceName, UUID correlationId) {
		return nodes.stream()
					.filter(n -> n	.getCorrelationID()
									.equals(correlationId)
							&& n.getServiceName()
								.equals(serviceName))
					.findAny()
					.orElse(null);
	}

	private static List<LogNode> convertLogEntryToLogNode(List<LogEntry> entries) {
		List<LogNode> nodes = new ArrayList<LogNode>();
		for (LogEntry s : entries) {
			LogNode result = searchLogNodes(nodes, s.serviceName(), s.correlationID());
			if (result == null) {
				result = new LogNode(s);
				nodes.add(result);
			}
			result.addTimestamp(s.timestamp());
		}
		return nodes;
	}

	private static List<LogEntry> searchForCorrelationId(List<LogEntry> entries, UUID correlationId) {
		return entries	.stream()
						.filter(e -> e	.correlationID()
										.equals(correlationId))
						.collect(Collectors.toList());
	}

	public static void printListLogNodes(List<LogNode> nodes) {
		nodes.forEach(System.out::println);
	}

	private static Graph<LogNode, DefaultEdge> createLogNodeGraph(List<LogNode> nodes) {
		Graph<LogNode, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

		for (LogNode n : nodes) {
			g.addVertex(n);
		}

		for (LogNode vertix : nodes) {
			LogNode parent = null;
			for (LogNode n : nodes) {
				if (vertix.getDuration() < n.getDuration() && vertix.getEntryTimestamp()
																	.isAfter(n.getEntryTimestamp())
						&& vertix	.getExitTimestamp()
									.isBefore(n.getExitTimestamp())) {
					if (parent == null || n.getDuration() < parent.getDuration())
						parent = n;
				}
			}
			if (parent != null) {
				g.addEdge(parent, vertix);
				System.out.println("Add edge between :" + parent.getServiceName() + " and " + vertix.getServiceName());
			}
		}

		return g;
	}

	private static Renderer graphRenderer(Graph<LogNode, DefaultEdge> graph) throws ExportException {

		DOTExporter<LogNode, DefaultEdge> exporter = new DOTExporter<>(LogNode::getServiceName);
		exporter.setVertexAttributeProvider((v) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(v.getServiceName()));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(graph, writer);
		String dotRepresentation = writer.toString();

		try {
			MutableGraph g = new Parser().read(dotRepresentation);
			return Graphviz	.fromGraph(g)
							.render(Format.SVG);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error while processing DOT representation");
		}
	}

	private static List<LogEntry> stubGetGraph() {
		List<LogEntry> entries = new ArrayList<LogEntry>();
		UUID corrID = UUID.fromString("e8115e88-1350-4e25-9f64-04a396151e57");
		entries.add(new LogEntry("a", Instant.parse("2018-11-30T18:35:24.00Z"), corrID));
		entries.add(new LogEntry("a", Instant.parse("2018-11-30T18:37:24.00Z"), corrID));
		entries.add(new LogEntry("a", Instant.parse("2018-11-30T18:51:24.00Z"), corrID));
		entries.add(new LogEntry("b", Instant.parse("2018-11-30T18:39:24.00Z"), corrID));
		entries.add(new LogEntry("b", Instant.parse("2018-11-30T18:50:24.00Z"), corrID));
		entries.add(new LogEntry("c", Instant.parse("2018-11-30T18:39:27.00Z"), corrID));
		entries.add(new LogEntry("c", Instant.parse("2018-11-30T18:41:24.00Z"), corrID));
		entries.add(new LogEntry("d", Instant.parse("2018-11-30T18:42:24.00Z"), corrID));
		entries.add(new LogEntry("d", Instant.parse("2018-11-30T18:43:24.00Z"), corrID));
		entries.add(new LogEntry("d", Instant.parse("2018-11-30T18:44:24.00Z"), corrID));
		entries.add(new LogEntry("q", Instant.parse("2018-11-30T18:44:24.00Z"), UUID.randomUUID()));
		return entries;
	}

	private static Graph<LogNode, DefaultEdge> getGraph(List<LogEntry> logs, UUID uuid) {
		List<LogEntry> filteredEntries = uuid != null ? searchForCorrelationId(logs, uuid) : logs;

		List<LogNode> nodes = convertLogEntryToLogNode(filteredEntries);
		printListLogNodes(nodes);

		return createLogNodeGraph(nodes);
	}

	public static InputStream getGraphImage(List<LogEntry> logs, UUID uuid) {
		Graph<LogNode, DefaultEdge> logNodeGraph = getGraph(logs, uuid);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			graphRenderer(logNodeGraph).toOutputStream(bos);
			return new ByteArrayInputStream(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		Graph<LogNode, DefaultEdge> logNodeGraph = getGraph(stubGetGraph(),
				UUID.fromString("e8115e88-1350-4e25-9f64-04a396151e57"));

		try {
			graphRenderer(logNodeGraph).toFile(new File("examples/graph.svg"));
		} catch (ExportException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't render the file", e);
		}
	}

}