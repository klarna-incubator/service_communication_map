package com.klarna.secoma;

import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Component;

@Component
public class CommunicationMapService {

    public StreamResource findCommunicationMap(String correlationId) {
        System.out.println(String.format("### Correlation ID: %s", correlationId));
        return null;
    }

}
