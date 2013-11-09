package com.travel.map;

public class Coordinate {

	private double lat;
	private double lng;
	
	public Coordinate(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLang() {
		return lng;
	}
	public void setLang(double lng) {
		this.lng = lng;
	}
	
	@Override
	public String toString() {
		return lat + "," + lng;
	}
}
