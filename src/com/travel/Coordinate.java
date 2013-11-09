package com.travel;

public class Coordinate {

	private double lat;
	private double lng;
	private boolean isTrainChange;
	

	public Coordinate(double lat, double lng, boolean isTrainChange) {
		this.lat = lat;
		this.lng = lng;
		this.isTrainChange = isTrainChange;
	}
	public boolean isTrainChange() {
		return isTrainChange;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
}
