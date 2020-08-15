package com.klarna.secoma;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

@Component
public class CommunicationMapService {

	public StreamResource findCommunicationMap(String correlationId) {
		System.out.println(String.format("### Correlation ID: %s", correlationId));
		InputStreamFactory x = new InputStreamFactory() {
			private static final long serialVersionUID = 9031433761422193929L;

			@Override
			public InputStream createInputStream() {
				return NodeGraph.getGraphImage();
			}
		};
		return new StreamResource("foo.png", x);
	}
}
