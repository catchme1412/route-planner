package com.travel;

import java.util.Map;

public class Results {

	private Map<String, Long> timeMap;
	private Map<String, Double> costMap;
	private Map<String, Double> comfortMap;
	
private String shortestPath;
private String shortestPathCost;
private String shortestPathDuration;
private String shortestPathComfort;
	
	
	public String getShortestPath() {
	return shortestPath;
}
public void setShortestPath(String shortestPath) {
	this.shortestPath = shortestPath;
}
public String getShortestPathCost() {
	return shortestPathCost;
}
public void setShortestPathCost(String shortestPathCost) {
	this.shortestPathCost = shortestPathCost;
}
public String getShortestPathDuration() {
	return shortestPathDuration;
}
public void setShortestPathDuration(String shortestPathDuration) {
	this.shortestPathDuration = shortestPathDuration;
}
public String getShortestPathComfort() {
	return shortestPathComfort;
}
public void setShortestPathComfort(String shortestPathComfort) {
	this.shortestPathComfort = shortestPathComfort;
}
	public Map<String, Long> getTimeMap() {
		return timeMap;
	}
	public void setTimeMap(Map<String, Long> timeMap) {
		this.timeMap = timeMap;
	}
	public Map<String, Double> getCostMap() {
		return costMap;
	}
	public void setCostMap(Map<String, Double> costMap) {
		this.costMap = costMap;
	}
	public Map<String, Double> getComfortMap() {
		return comfortMap;
	}
	public void setComfortMap(Map<String, Double> comfortMap) {
		this.comfortMap = comfortMap;
	}
}