package com.klarna.secoma;

import static java.lang.String.format;

import java.time.Instant;
import java.util.UUID;

public class SimpleLogEntry implements LogEntry{
	private String serviceName;
	
	@Deprecated
	private final String _raw;
	
	
	public SimpleLogEntry(String[] data) {
		this.serviceName = data[0];
		this._raw = data[1];
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
		return format("# %s ## %s", this.serviceName, this._raw);
		
	}

}
