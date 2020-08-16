package com.klarna.secoma.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties 
public class Configuration {

	private String logsLocation;

	public String getLogsLocation() {
		return logsLocation;
	}

	public void setLogsLocation(String logsLocation) {
		this.logsLocation = logsLocation;
	}
	
	
}
