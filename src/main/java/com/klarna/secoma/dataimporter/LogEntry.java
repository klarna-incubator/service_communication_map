package com.klarna.secoma.dataimporter;

import java.time.Instant;
import java.util.UUID;

public record LogEntry(String serviceName, Instant timestamp, UUID correlationID) {
}