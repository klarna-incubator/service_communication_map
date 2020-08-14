package com.klarna.secoma;

import static java.lang.String.format;

import java.time.Instant;
import java.util.UUID;

public class SimpleLogEntry implements LogEntry{
	private String serviceName;
	private UUID correlationID;
	private Instant timestamp;	
	
	public SimpleLogEntry(String[] data) {
		this.serviceName = data[0];
		this.correlationID = UUID.fromString(data[1]);
		this.timestamp = Instant.parse(data[2]);
	}
	
	@Override
	public UUID getCorrelationID() {
		return correlationID;
	}

	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	@Override
	public Instant getTimestamp() {
		return timestamp;
	}
	
	public String toString() {
		return format("# %s ## %s ## %s", this.serviceName, this.correlationID, this.timestamp);
		
	}

}
