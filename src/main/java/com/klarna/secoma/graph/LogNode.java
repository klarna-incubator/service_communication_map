package com.klarna.secoma.graph;

import static java.lang.String.format;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import com.klarna.secoma.dataimporter.LogEntry;

public class LogNode {
	private String serviceName;
	private UUID correlationID;
	private ArrayList<Instant> timestamps;	
	private Instant entryTimestamp;	
	private Instant exitTimestamp;	
	
	public LogNode(LogEntry s) {
		this.serviceName = s.serviceName();
		this.correlationID = s.correlationID();
		this.timestamps = new ArrayList<>();
	}
	
	public UUID getCorrelationID() {
		return correlationID;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public ArrayList<Instant> getTimestamps() {
		return timestamps;
	}

	public Instant getEntryTimestamp() {
		return entryTimestamp;
	}

	public void setEntryTimestamp(Instant time) {
		if(this.entryTimestamp == null || this.entryTimestamp.isAfter(time))
			this.entryTimestamp = time;
	}

	public Instant getExitTimestamp() {
		return exitTimestamp;
	}

	public void setExitTimestamp(Instant time) {
		if(this.exitTimestamp == null || this.exitTimestamp.isBefore(time))
			this.exitTimestamp = time;
	}

	public long getDuration() {
		return ChronoUnit.MILLIS.between(entryTimestamp, exitTimestamp);
	}

	public void addTimestamp(Instant timestamp) {
		timestamps.add(timestamp);
		setEntryTimestamp(timestamp);
		setExitTimestamp(timestamp);
	}
	
	public String toString() {
		String listString = "";
		for (Instant s : this.timestamps)
		{
			listString += s + "\t";
		}
		return format("#### NODE #### \n## service name: %s ## correlation id: %s \n## timestamps: %s \n## entryTimestamp: %s ## exitTimestamp: %s ## duration: %s", this.serviceName, this.correlationID, listString,
				this.entryTimestamp, this.exitTimestamp, this.getDuration());
	}
}
