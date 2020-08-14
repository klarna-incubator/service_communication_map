package com.klarna.secoma;

import java.util.ArrayList;
import java.util.UUID;

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
            if (result == null){
                result = new LogNode(s);
                nodes.add(result);
            }
            result.addTimestamp(s.getTimestamp());
        }
        return nodes;
    }

    public static String printArrayListLogNodes(ArrayList<LogNode> nodes) {
		String listString = "";
		for (LogNode n: nodes)
		{
			listString += n.toString() + "\n";
		}
        return listString;
	}

    public static void main(String[] args) {
        ArrayList<LogEntry> entries = new ArrayList<LogEntry>();
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:35:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:37:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"a","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:38:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"b","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:39:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"b","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:38:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"c","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:39:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"c","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:41:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:42:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:43:24.00Z"}));
        entries.add(new SimpleLogEntry(new String[]{"d","e8115e88-1350-4e25-9f64-04a396151e57", "2018-11-30T18:44:24.00Z"}));
        
        System.out.println(printArrayListLogNodes(convertLogEntryToLogNode(entries)));
        }
    
}