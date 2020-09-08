package com.klarna.secoma.dataimporter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record LogEntry(String serviceName, Instant timestamp, Map<String,UUID> ids, UUID correlationID) {
}