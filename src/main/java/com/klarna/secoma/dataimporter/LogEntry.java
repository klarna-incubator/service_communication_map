package com.klarna.secoma.dataimporter;

import java.time.Instant;
import java.util.UUID;

public interface LogEntry {

	public UUID getCorrelationID();

	public String getServiceName();

	public Instant getTimestamp();

}