package org.neo4j.examples.astarrouting;

import org.neo4j.graphdb.Node;

public class SearchCriteria {

	private Node source;
	private Node destination;
	private String priority;

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getDestination() {
		return destination;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

}
