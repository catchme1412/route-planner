package org.neo4j.examples.astarrouting;

import java.util.Date;

import org.neo4j.graphdb.Relationship;

public class RailRouteRelationship {

	private Relationship route;//train name,train number convinience
	
	public RailRouteRelationship (Relationship route) {
		this.route = route;
	}
	public Relationship getRoute() {
		return route;
	}

	public void setRoute(Relationship route) {
		this.route = route;
	}
	public void setDeparture(Date t) {
		route.setProperty("departure", t);
	}
	
	public Date getDeparture() {
		return (Date) route.getProperty("departure");
	}
	
	public void setCost (Double d) {
		route.setProperty("cost", d);
	}
	
	public Double getCost (Relationship route) {
		return (Double) route.getProperty("cost");
		
	}
	
	public void setDistance (Double d) {
		route.setProperty("distance", d);
	}
	
	public Double getDistance (Relationship route) {
		return (Double) route.getProperty("distance");
		
	}	
}
