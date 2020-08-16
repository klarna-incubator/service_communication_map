package com.klarna.secoma.dataimporter;

import static java.lang.String.format;

import java.time.Instant;
import java.util.UUID;

public class SimpleLogEntry implements LogEntry{

	private final String serviceName;
	private final Instant timestamp;
	private final UUID correlationID;
	
	
	
	public SimpleLogEntry(String serviceName, Instant timestamp, UUID correlationID) {
		this.serviceName = serviceName;
		this.timestamp = timestamp;
		this.correlationID = correlationID;
	}
	
	@Override
	public UUID getCorrelationID() {
		return this.correlationID;
	}

	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	@Override
	public Instant getTimestamp() {
		return this.timestamp;
	}
	
	public String toString() {
		return format("# %s <%s> @ %s", this.serviceName, this.correlationID, this.timestamp);
	}

}
