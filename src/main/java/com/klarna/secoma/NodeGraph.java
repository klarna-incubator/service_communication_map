package com.klarna.secoma;

import java.util.ArrayList;
import java.util.UUID;
import org.jgrapht.*;
import org.jgrapht.graph.*;
public class NodeGraph {


    private static LogNode searchLogNodes(ArrayList<LogNode> nodes, String serviceName, UUID correlationId) {
        for (LogNode n : nodes) {
            if(n.getCorrelationID().equals(correlationId) && n.getServiceName().equals(serviceName))
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
            if (e.getCorrelationID().equals(correlationId)){
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
            if(parent != null){
                g.addEdge(parent, vertix);
                System.out.println("Add edge between :" + parent.getServiceName() + " and "+ vertix.getServiceName() );
            }
        }        

        return g;
    }

    public static void main(String[] args) {
        ArrayList<LogEntry> entries = new ArrayList<LogEntry>();
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:35:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:37:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:51:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"b","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:39:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"b","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:50:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"c","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:39:27.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"c","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:41:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:42:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:43:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:44:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"q","e8115e88-1350-4e25-9f64-04a396151eaa", "2018-11-30T18:44:24.00Z"}));
        
        ArrayList<LogEntry> filteredEntries = searchForCorrelationId(entries,  UUID.fromString("e8115e88-1350-4e25-9f64-04a396151e57"));

        ArrayList<LogNode> nodes = convertLogEntryToLogNode(filteredEntries);
        printArrayListLogNodes(nodes);

        createLogNodeGraph(nodes);
    }
    
}