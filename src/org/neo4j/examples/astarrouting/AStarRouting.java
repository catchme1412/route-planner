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

import java.util.Iterator;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;

public class AStarRouting {
	private static final EstimateEvaluator<Double> estimateEval = CommonEvaluators.geoEstimateEvaluator(
			RailwayStation.LATITUDE, RailwayStation.LONGITUDE);
	// TODO create a custom cost evaluator
	private static final CostEvaluator<Double> costEval = CommonEvaluators.doubleCostEvaluator(RailwayStation.COST);

	public static void main(final String[] args) {
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase("target/neo4j-db");
		try {
			routing(graphDb);
		} finally {
			graphDb.shutdown();
		}
	}

	/**
	 * Creating the graph
	 * 
	 * @param graphDb
	 */
	private static void routing(final GraphDatabaseService graphDb) {
		Transaction tx = graphDb.beginTx();
		RailwayStation BLR, SALEM, CHENNAI, DEL, SF;
		try {
			BLR = new RailwayStation(graphDb, "Bangalore", "Karnataka");
			SALEM = new RailwayStation(graphDb, "Salem", "TamilNadu");
			CHENNAI = new RailwayStation(graphDb, "Chennai", "TamilNadu");
			DEL = new RailwayStation(graphDb, "Delhi", "Delhi");
			// SF = new RailwayStation(graphDb, "San Fransisco", "CA");
			BLR.createRailRouteTo(SALEM);
			BLR.createRailRouteTo(CHENNAI);
			// SEA.createRailRouteTo(SF);
			SALEM.createRailRouteTo(CHENNAI);
			CHENNAI.createRailRouteTo(DEL);
			DEL.createRailRouteTo(BLR);
			tx.success();
		} finally {
			tx.finish();
		}
		tx = graphDb.beginTx();
		try {
			Expander relExpander = Traversal.expanderForTypes(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
			relExpander.add(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
			PathFinder<WeightedPath> sp = GraphAlgoFactory.aStar(relExpander, costEval, estimateEval);
			Path path = sp.findSinglePath(BLR.getUnderlyingNode(), CHENNAI.getUnderlyingNode());
			System.out.println("SHORTEST PATH IS GIVEN BELOW:");
			for (Node node : path.nodes()) {
				System.out.print("=>" + node.getProperty(RailwayStation.NAME));
			}

			Iterable<WeightedPath> allPaths = sp.findAllPaths(BLR.getUnderlyingNode(), CHENNAI.getUnderlyingNode());
			System.out.println("\nALL PATHS:::: ");
			for (WeightedPath p : allPaths) {
				System.out.println("PATH:");
				for (Iterator<Node> iterator = p.nodes().iterator(); iterator.hasNext();) {
					Node nodes = iterator.next();
					System.out.print("==>" + nodes.getProperty(RailwayStation.NAME));
				}
			}

			System.out.println("\nALL SIMPLE PATHS:::");
			PathFinder<Path> p2 = GraphAlgoFactory.allSimplePaths(relExpander, 100);
			Iterable<Path> mm = p2.findAllPaths(BLR.getUnderlyingNode(), CHENNAI.getUnderlyingNode());
			for (Path m : mm) {
				System.out.println("\nNEW PATH:" + m.length());
				for (Node n : m.nodes()) {
					System.out.print("==>" + n.getProperty(RailwayStation.NAME));
				}
			}

			tx.success();
		} finally {
			tx.finish();
		}
	}
}
