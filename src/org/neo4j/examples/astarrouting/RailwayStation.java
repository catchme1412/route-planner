/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.examples.astarrouting;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class RailwayStation {
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
	static final String NAME = "name";
	static final String COST = "cost";
	static final String STATION_CODE = "code";
	private final Node underlyingNode;
	private Coordinates coordinates;

	public RailwayStation(final Node node) {
		this.underlyingNode = node;
	}

	public RailwayStation(final GraphDatabaseService graphDb, final String city, final String state, String code, double latitude, double longtude) {
		this.underlyingNode = graphDb.createNode();
		// Rajesh need to be provided while creating the station
//		coordinates = new Coordinates();
		underlyingNode.setProperty(LATITUDE, latitude);
		underlyingNode.setProperty(LONGITUDE, longtude);
		underlyingNode.setProperty(NAME, city);
		underlyingNode.setProperty(STATION_CODE, code);
		System.out.println(this);
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	public String getName() {
		return (String) underlyingNode.getProperty(NAME);
	}

	public Coordinates getCoordinates() {
		// double latitude = (Double) underlyingNode.getProperty(LATITUDE);
		// double longitude = (Double) underlyingNode.getProperty(LONGITUDE);
		// return new Coordinates(latitude, longitude);
		return coordinates;
	}

	public Relationship createRailRouteTo(final RailwayStation other) {
		Relationship route = underlyingNode.createRelationshipTo(other.underlyingNode, RelationshipTypes.RAIL_ROUTE);
		route.setProperty(COST, GeoCostEvaluator.distance(underlyingNode, other.underlyingNode));
		return (route);
	}

	@Override
	public String toString() {
		return "RailwayStation [name=" + getName() + ", " + getCoordinates() + "]";
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
}
