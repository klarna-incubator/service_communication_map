package com.klarna.secoma;

import static java.lang.String.format;

import java.time.Instant;
import java.util.UUID;

public class SimpleLogEntry implements LogEntry{
	private final String serviceName;
	private final Instant timestamp;
	
	
	
	public SimpleLogEntry(String serviceName, Instant timestamp) {
		this.serviceName = serviceName;
		this.timestamp = timestamp;
	}
	
	@Override
	public UUID getCorrelationID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	@Override
	public Instant getTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return format("# %s ## %s", this.serviceName, this.timestamp);
		
	}

}
