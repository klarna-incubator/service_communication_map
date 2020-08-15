package com.klarna.secoma;

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
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.dot.DOTExporter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class NodeGraph {

	private static LogNode searchLogNodes(ArrayList<LogNode> nodes, String serviceName, UUID correlationId) {
		for (LogNode n : nodes) {
			if (n.getCorrelationID().equals(correlationId) && n.getServiceName().equals(serviceName))
				return n;
		}
		return null;
	}

	private static ArrayList<LogNode> convertLogEntryToLogNode(ArrayList<LogEntry> entries) {
		ArrayList<LogNode> nodes = new ArrayList<LogNode>();
		for (LogEntry s : entries) {
			LogNode result = searchLogNodes(nodes, s.getServiceName(), s.getCorrelationID());
			if (result == null) {
				result = new LogNode(s);
				nodes.add(result);
			}
			result.addTimestamp(s.getTimestamp());
		}
		return nodes;
	}

	private static ArrayList<LogEntry> searchForCorrelationId(ArrayList<LogEntry> entries, UUID correlationId) {
		ArrayList<LogEntry> nodes = new ArrayList<LogEntry>();
		for (LogEntry e : entries) {
			if (e.getCorrelationID().equals(correlationId)) {
				nodes.add(e);
			}
		}
		return nodes;
	}

	public static void printArrayListLogNodes(ArrayList<LogNode> nodes) {
		String listString = "";
		for (LogNode n : nodes) {
			listString += n.toString() + "\n";
		}
		System.out.println(listString);
	}

	private static Graph<LogNode, DefaultEdge> createLogNodeGraph(ArrayList<LogNode> nodes) {
		Graph<LogNode, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

		for (LogNode n : nodes) {
			g.addVertex(n);
		}

		for (LogNode vertix : nodes) {
			LogNode parent = null;
			for (LogNode n : nodes) {
				if (vertix.getDuration() < n.getDuration() && vertix.getEntryTimestamp().isAfter(n.getEntryTimestamp())
						&& vertix.getExitTimestamp().isBefore(n.getExitTimestamp())) {
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
			map.put("label", DefaultAttribute.createAttribute(v.toString()));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(graph, writer);
		String dotRepresentation = writer.toString();

		try {
			MutableGraph g = new Parser().read(dotRepresentation);
			return Graphviz.fromGraph(g).width(700).render(Format.SVG);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error while processing DOT representation");
		}
	}

	private static Graph<LogNode, DefaultEdge> stubGetGraph() {
		ArrayList<LogEntry> entries = new ArrayList<LogEntry>();
		UUID corrID = UUID.fromString("e8115e88-1350-4e25-9f64-04a396151e57");
		entries.add(new SimpleLogEntry("a", Instant.parse("2018-11-30T18:35:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("a", Instant.parse("2018-11-30T18:37:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("a", Instant.parse("2018-11-30T18:51:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("b", Instant.parse("2018-11-30T18:39:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("b", Instant.parse("2018-11-30T18:50:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("c", Instant.parse("2018-11-30T18:39:27.00Z"), corrID));
		entries.add(new SimpleLogEntry("c", Instant.parse("2018-11-30T18:41:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("d", Instant.parse("2018-11-30T18:42:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("d", Instant.parse("2018-11-30T18:43:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("d", Instant.parse("2018-11-30T18:44:24.00Z"), corrID));
		entries.add(new SimpleLogEntry("q", Instant.parse("2018-11-30T18:44:24.00Z"), UUID.randomUUID()));

		ArrayList<LogEntry> filteredEntries = searchForCorrelationId(entries,
				UUID.fromString("e8115e88-1350-4e25-9f64-04a396151e57"));

		ArrayList<LogNode> nodes = convertLogEntryToLogNode(filteredEntries);
		printArrayListLogNodes(nodes);

		Graph<LogNode, DefaultEdge> logNodeGraph = createLogNodeGraph(nodes);
		return logNodeGraph;
	}

	public static InputStream getGraphImage() {
		Graph<LogNode, DefaultEdge> logNodeGraph = stubGetGraph();

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(graphRenderer(logNodeGraph).toImage(), "png", bos);
			return new ByteArrayInputStream(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		Graph<LogNode, DefaultEdge> logNodeGraph = stubGetGraph();

		try {
			graphRenderer(logNodeGraph).toFile(new File("examples/graph.svg"));
		} catch (ExportException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't render the file", e);
		}
	}

}