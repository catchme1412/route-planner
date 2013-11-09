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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;
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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;
import org.neo4j.tooling.GlobalGraphOperations;

import com.travel.Coordinate;
import com.travel.Results;

public class AStarRouting {
	private static final EstimateEvaluator<Double> estimateEval = CommonEvaluators.geoEstimateEvaluator(
			RailwayStation.LATITUDE, RailwayStation.LONGITUDE);
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;

	// TODO create a custom cost evaluator
	private static final CostEvaluator<Double> costEval = new RouteCostEvaluator();// CommonEvaluators.doubleCostEvaluator(RailwayStation.COST);

	private static GraphDatabaseService graphDb;
	private static final String dbPath = "/tmp/neo4j-db";

	public AStarRouting() {
		File file = new File(dbPath);
		file.deleteOnExit();
		setGraphDb(new EmbeddedGraphDatabase(dbPath));
		registerShutdownHook(getGraphDb());
		try {
			// routing(getGraphDb());

			countNodes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void countNodes() {
		Transaction tx = getGraphDb().beginTx();
		int i = 0;
		try {

			for (Node n : GlobalGraphOperations.at(getGraphDb()).getAllNodes()) {
				i++;
			}
			System.out.println("TOTAL NODES:" + i);
			tx.success();
		} finally {
			tx.finish();
		}

	}

	public Node getNode(String stationCode) {
		Transaction tx = getGraphDb().beginTx();
		try {
			for (Node n : GlobalGraphOperations.at(getGraphDb()).getAllNodes()) {
				try {
					System.out.println("getNode:" + n.getId());
					System.out.println(">>>>>>>>" + n.getProperty("code"));
				} catch (Exception e) {
					// temp fix: delete all the unwanted nodes and relationships
					for (Relationship r : n.getRelationships()) {
						r.delete();
					}
					n.delete();
					continue;
				}
				// System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGgg:");
				// System.out.println(">>>>>>>>>>"+n.getProperty("trainName"));
				if (n.getProperty("code").equals(stationCode)) {
					return n;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
		return null;

	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public static void main(final String[] args) throws ParseException {
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase(dbPath);
		try {
			// routing(graphDb);
		} finally {
			graphDb.shutdown();
			new File(dbPath).deleteOnExit();
		}
	}

	/**
	 * Creating the graph
	 * 
	 * @param graphDb
	 * @throws ParseException
	 */
	// private static void routing(final GraphDatabaseService graphDb) throws
	// ParseException {
	// Transaction tx = graphDb.beginTx();
	// RailwayStation BLR_SBC = null, BLR_CANTT = null, SALEM = null, CHENNAI =
	// null, DEL = null, EKM = null, MV = null;
	// try {
	// //BNC, SBC, SA, MAS, DLI, ERS, MV
	// BLR_CANTT = new RailwayStation(graphDb, "Bangalore Cantt", "Karnataka",
	// "BNC", 12.9921351, 77.60056850000001);
	// BLR_SBC = new RailwayStation(graphDb, "Bangalore City", "Karnataka",
	// "SBC", 12.976549900000000000, 77.568811500000040000);
	// SALEM = new RailwayStation(graphDb, "Salem", "TamilNadu", "SA",
	// 11.664325000000000000, 78.146014199999970000);
	// CHENNAI = new RailwayStation(graphDb, "Chennai", "TamilNadu", "MAS",
	// 13.060422000000000000, 80.249583000000030000);
	// DEL = new RailwayStation(graphDb, "Delhi", "Delhi", "DLI",
	// 28.635308000000000000, 77.224960000000010000 );
	// EKM = new RailwayStation(graphDb, "Eranakulam", "Kerala", "ERS",
	// 9.970101699999999000, 76.291173999999960000);
	// MV = new RailwayStation(graphDb, "Mailadudurai", "Tamil Nadu", "MV",
	// 11.094908200000000000, 79.628278700000010000);
	//
	// // /**
	// // * BLR_SBC to BLR_CANTT - Eranakulam Express
	// // */
	// Relationship BLR_SBC2BLR_CANTT_EE = BLR_SBC.createRailRouteTo(BLR_CANTT);
	// BLR_SBC2BLR_CANTT_EE.setProperty("trainName", "Eranakulam Express");
	// BLR_SBC2BLR_CANTT_EE.setProperty("trainNumber", 12677);
	// BLR_SBC2BLR_CANTT_EE.setProperty("trainDeparture", "06:15");
	// BLR_SBC2BLR_CANTT_EE.setProperty("trainArrival", "06:25");
	// BLR_SBC2BLR_CANTT_EE.setProperty("trainComfortRating", .5d);
	// BLR_SBC2BLR_CANTT_EE.setProperty("cost", 5D);
	// BLR_SBC2BLR_CANTT_EE.setProperty("distance", 5D);
	// // /**
	// // * BLR_CANTT to SALEM - Eranakulam Exp
	// // */
	// Relationship BLR_CANTT2SA = BLR_CANTT.createRailRouteTo(SALEM);
	// BLR_CANTT2SA.setProperty("trainName", "Eranakulam Express");
	// BLR_CANTT2SA.setProperty("trainNumber", 12677);
	// BLR_CANTT2SA.setProperty("trainDeparture", "06:15");// correct time
	// // TODO
	// BLR_CANTT2SA.setProperty("trainArrival", "10:02");
	// BLR_CANTT2SA.setProperty("trainComfortRating", .5d);
	// BLR_CANTT2SA.setProperty("cost", 213d);
	// BLR_CANTT2SA.setProperty("distance", 213d);
	//
	// /**
	// * SALEM to Eranakulam - Eranakulam Exp
	// */
	// Relationship SA2EKM = SALEM.createRailRouteTo(EKM);
	// SA2EKM.setProperty("trainName", "Eranakulam Express");
	// SA2EKM.setProperty("trainNumber", 12677);
	// SA2EKM.setProperty("trainDeparture", "10:05");
	// SA2EKM.setProperty("trainArrival", "16:55");
	// SA2EKM.setProperty("trainComfortRating", .5d);
	// SA2EKM.setProperty("cost", 369d);
	// SA2EKM.setProperty("distance", 369d);
	//
	// // /**
	// // * BLR_SBC to BLR_CANTT - Mailadudurai Express
	// // */
	// Relationship BLR_SBC2BLR_CANTT_ME = BLR_SBC.createRailRouteTo(BLR_CANTT);
	// BLR_SBC2BLR_CANTT_ME.setProperty("trainName", "Mailadudurai Express");
	// BLR_SBC2BLR_CANTT_ME.setProperty("trainNumber", 16232);
	// BLR_SBC2BLR_CANTT_ME.setProperty("trainDeparture", "06:15");
	// BLR_SBC2BLR_CANTT_ME.setProperty("trainArrival", "06:25");
	// BLR_SBC2BLR_CANTT_ME.setProperty("trainComfortRating", .5d);
	// BLR_SBC2BLR_CANTT_ME.setProperty("cost", 5D);
	// BLR_SBC2BLR_CANTT_ME.setProperty("distance", 5D);
	//
	// /**
	// * // * BLR_CANTT to SALEM - Mailadudurai Exp //
	// */
	// Relationship BLR_CANTT2SA_MAILADU = BLR_CANTT.createRailRouteTo(SALEM);
	// BLR_CANTT2SA_MAILADU.setProperty("trainName", "Mailadudurai Express");
	// BLR_CANTT2SA_MAILADU.setProperty("trainNumber", 16232);
	// BLR_CANTT2SA_MAILADU.setProperty("trainDeparture", "19:05");
	// BLR_CANTT2SA_MAILADU.setProperty("trainArrival", "23:42");
	// BLR_CANTT2SA_MAILADU.setProperty("trainComfortRating", .5d);
	// BLR_CANTT2SA_MAILADU.setProperty("cost", 214d);
	// BLR_CANTT2SA_MAILADU.setProperty("distance", 214d);
	//
	// /**
	// * // * SALEM to Mailadudurai - Mailadudurai Exp //
	// */
	// Relationship SA2TJV_MAILADU = SALEM.createRailRouteTo(MV);
	// SA2TJV_MAILADU.setProperty("trainName", "Mailadudurai Express");
	// SA2TJV_MAILADU.setProperty("trainNumber", 16232);
	// SA2TJV_MAILADU.setProperty("trainDeparture", "23:45");
	// SA2TJV_MAILADU.setProperty("trainArrival", "07:00");
	// SA2TJV_MAILADU.setProperty("trainComfortRating", .5d);
	// SA2TJV_MAILADU.setProperty("cost", 323d);
	// SA2TJV_MAILADU.setProperty("distance", 323d);
	//
	// /**
	// * BLR_SBC to BLR_CANTT - KARNATAKA EXP
	// */
	// Relationship BLR_SBC2BLR_CANTT_KE = BLR_SBC.createRailRouteTo(BLR_CANTT);
	// BLR_SBC2BLR_CANTT_KE.setProperty("trainName", "KARNATAKA EXP");
	// BLR_SBC2BLR_CANTT_KE.setProperty("trainNumber", 12627);
	// BLR_SBC2BLR_CANTT_KE.setProperty("trainDeparture", "19:20");
	// BLR_SBC2BLR_CANTT_KE.setProperty("trainArrival", "19:30");
	// BLR_SBC2BLR_CANTT_KE.setProperty("trainComfortRating", .5d);
	// BLR_SBC2BLR_CANTT_KE.setProperty("cost", 5D);
	// BLR_SBC2BLR_CANTT_KE.setProperty("distance", 5D);
	//
	// /**
	// * BLR_CANTT to Delhi - KARNATAKA EXP
	// */
	// Relationship BLR_CANTT2DEL = BLR_CANTT.createRailRouteTo(DEL);
	// BLR_CANTT2DEL.setProperty("trainName", "KARNATAKA EXP");
	// BLR_CANTT2DEL.setProperty("trainNumber", 12627);
	// BLR_CANTT2DEL.setProperty("trainDeparture", "19:32");
	// BLR_CANTT2DEL.setProperty("trainArrival", "10:30");
	// BLR_CANTT2DEL.setProperty("trainComfortRating", .5d);
	// BLR_CANTT2DEL.setProperty("cost", 2401d);
	// BLR_CANTT2DEL.setProperty("distance", 2401d);
	//
	// /**
	// * BLR_SBC to BLR_CANTT - LALBAGH EXP
	// */
	// Relationship BLR_SBC2BLR_CANTT_LE = BLR_SBC.createRailRouteTo(BLR_CANTT);
	// BLR_SBC2BLR_CANTT_LE.setProperty("trainName", "LALBAGH EXP");
	// BLR_SBC2BLR_CANTT_LE.setProperty("trainNumber", 12608);
	// BLR_SBC2BLR_CANTT_LE.setProperty("trainDeparture", "06:30");
	// BLR_SBC2BLR_CANTT_LE.setProperty("trainArrival", "06:40");
	// BLR_SBC2BLR_CANTT_LE.setProperty("trainComfortRating", .5d);
	// BLR_SBC2BLR_CANTT_LE.setProperty("cost", 5D);
	// BLR_SBC2BLR_CANTT_LE.setProperty("distance", 5D);
	//
	// /**
	// * BLR_CANTT to Chennai - LALBAGH EXP
	// */
	// Relationship BLR_CANTT2SA_LALBAGH = BLR_CANTT.createRailRouteTo(SALEM);
	// BLR_CANTT2SA_LALBAGH.setProperty("trainName", "LALBAGH EXP");
	// BLR_CANTT2SA_LALBAGH.setProperty("trainNumber", 12608);
	// BLR_CANTT2SA_LALBAGH.setProperty("trainDeparture", "06:42");
	// BLR_CANTT2SA_LALBAGH.setProperty("trainArrival", "12:15");
	// BLR_CANTT2SA_LALBAGH.setProperty("trainComfortRating", .5d);
	// BLR_CANTT2SA_LALBAGH.setProperty("cost", 357d);
	// BLR_CANTT2SA_LALBAGH.setProperty("distance", 357d);
	//
	// /**
	// * CHENNAI to SALEM - TRIVANDRUM MAIL
	// */
	// Relationship CHENNAI2SA = CHENNAI.createRailRouteTo(SALEM);
	// CHENNAI2SA.setProperty("trainName", "TRIVANDRUM MAIL");
	// CHENNAI2SA.setProperty("trainNumber", 12623);
	// CHENNAI2SA.setProperty("trainDeparture", "19:45");
	// CHENNAI2SA.setProperty("trainArrival", "00:05");
	// CHENNAI2SA.setProperty("trainComfortRating", .5d);
	// CHENNAI2SA.setProperty("cost", 334d);
	// CHENNAI2SA.setProperty("distance", 334d);
	//
	// /**
	// * SALEM to Eranakulam - TRIVANDRUM MAIL
	// */
	// Relationship SA2EKM_TM = SALEM.createRailRouteTo(EKM);
	// SA2EKM_TM.setProperty("trainName", "TRIVANDRUM MAIL");
	// SA2EKM_TM.setProperty("trainNumber", 12623);
	// SA2EKM_TM.setProperty("trainDeparture", "00:10");
	// SA2EKM_TM.setProperty("trainArrival", "06:20");
	// SA2EKM_TM.setProperty("trainComfortRating", .5d);
	// SA2EKM_TM.setProperty("cost", 687d - 334d);
	// SA2EKM_TM.setProperty("distance", 687d - 334d);
	// tx.success();
	// } finally {
	// tx.finish();
	// }
	// Transaction tx2 = graphDb.beginTx();
	// try {
	// Expander relExpander =
	// Traversal.expanderForTypes(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
	// relExpander.add(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
	// PathFinder<WeightedPath> sp = GraphAlgoFactory.aStar(relExpander,
	// costEval, estimateEval);
	// Path path = sp.findSinglePath(BLR_SBC.getUnderlyingNode(),
	// EKM.getUnderlyingNode());
	// System.out.println("SHORTEST PATH IS GIVEN BELOW:");
	// for (Node node : path.nodes()) {
	// System.out.print("=>" + node.getProperty(RailwayStation.NAME));
	// }
	//
	//
	// /*// System.out.print("\nBLR==>");
	// for (Relationship r : path.relationships()) {
	// proposedPath.append("==>");
	// proposedPath.append(r.getProperty("trainDeparture"));
	// proposedPath.append("::::");
	// proposedPath.append(r.getStartNode().getProperty("name"));
	// proposedPath.append("---");
	// proposedPath.append(r.getEndNode().getProperty("name"));
	//
	// * System.out.println("==>" + r.getProperty("trainName") +
	// * "::::" +r.getStartNode().getProperty("name") + "---"
	// * +r.getEndNode().getProperty("name") );
	//
	// }
	// System.out.println(proposedPath);*/
	//
	// Iterable<WeightedPath> allPaths =
	// sp.findAllPaths(BLR_SBC.getUnderlyingNode(), EKM.getUnderlyingNode());
	// System.out.println("\nALL PATHS:::: ");
	// for (WeightedPath p : allPaths) {
	// System.out.println("PATH:");
	// for (Iterator<Node> iterator = p.nodes().iterator(); iterator.hasNext();)
	// {
	// Node nodes = iterator.next();
	// System.out.print("==>" + nodes.getProperty(RailwayStation.NAME));
	// }
	// }
	//
	// System.out.println("\nALL SIMPLE PATHS:::");
	//
	// PathFinder<Path> p2 = GraphAlgoFactory.allSimplePaths(relExpander, 100);
	// Iterable<Path> mm = p2.findAllPaths(BLR_SBC.getUnderlyingNode(),
	// EKM.getUnderlyingNode());
	// for (Path m : mm) {
	// /*
	// * System.out.println(
	// * ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	// */
	// // StringBuffer proposedPath = new StringBuffer();
	// // System.out.print("\nBLR==>");
	// StringBuffer proposedPath = new StringBuffer();
	// for (Relationship r : m.relationships()) {
	// proposedPath.append("==>");
	// proposedPath.append(r.getProperty("trainName"));
	// proposedPath.append("::::");
	// proposedPath.append(r.getStartNode().getProperty("name"));
	// proposedPath.append("---");
	// proposedPath.append(r.getEndNode().getProperty("name"));
	// /*
	// * System.out.println("==>" + r.getProperty("trainName") +
	// * "::::" +r.getStartNode().getProperty("name") + "---"
	// * +r.getEndNode().getProperty("name") );
	// */
	// }
	//
	// Double totalTotalCost = calculateCost(m);
	// Double totalTotalDistance = calculateTotalDistance(m);
	// Double totalTotalComfortIndex = calculateComfortIndex(m);
	// long duration = calculateDuration(m);
	// int trainns = calculateNoOfTrain(m);
	//
	// StringBuffer actualTime = calculateRealTime(duration);
	//
	// sortBasedOnCost.put(proposedPath.toString(), totalTotalCost);
	// sortBasedOnTime.put(proposedPath.toString(), duration);
	// sortBasedOnComfort.put(proposedPath.toString(), totalTotalComfortIndex);
	//
	// /*
	// * System.out.println("Total Cost."+totalTotalCost);
	// * System.out.println("Distance."+totalTotalDistance);
	// * System.out.println("Comfort."+totalTotalComfortIndex);
	// * System.out.println("Duration"+actualTime);
	// */
	// /*
	// * System.out.println(
	// * ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	// */
	// }
	//
	// Map<String, Double> sortedCostMap = sortByComparator(sortBasedOnCost);
	// Map<String, Long> sortedTimeMap = sortByComparator(sortBasedOnTime);
	// System.out.println("======================================================");
	// printMap(sortedCostMap);
	// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	// printTimeMap(sortedTimeMap);
	// System.out.println("======================================================");
	//
	// tx2.success();
	// } finally {
	// tx2.finish();
	// }
	// }

	private static Double calculateCost(Path m) {
		Double totalTimeCost = 0.0;
		// ArrayList<Double> costOfRoute = new ArrayList<Double>();
		for (Relationship r : m.relationships()) {
			totalTimeCost += (Double) r.getProperty("cost");
		}
		// for (int i=0;i<costOfRoute.size();i++) {
		// totalTimeCost = totalTimeCost + costOfRoute.get(i);
		// }

		return totalTimeCost;
	}
	
	private List<Coordinate>  calculateCoordinates(Path m) {
		List <Coordinate> coordinates = new ArrayList<Coordinate>();
		List<Relationship> relationshipList = new ArrayList<Relationship>();
		
		for (Relationship r : m.relationships()) {
			relationshipList.add(r);
		}
		for (int i = 0; i < relationshipList.size() - 1; i++) {
			Relationship arrive = relationshipList.get(i);
			Relationship depart = relationshipList.get(i+1);
			String arriveTrainNumber = arrive.getProperty("trainNumber").toString();
			String departTrainNumber = depart.getProperty("trainNumber").toString();
//			// train change
			if (arriveTrainNumber.equals(departTrainNumber)) {
				Double latArrive = Double.parseDouble(arrive.getStartNode().getProperty("lat").toString());
				Double lonArrive = Double.parseDouble(arrive.getStartNode().getProperty("lon").toString());
				Coordinate cArrive = new Coordinate(latArrive, lonArrive, false);
				
				Double latDest = Double.parseDouble(arrive.getEndNode().getProperty("lat").toString());
				Double lonDest = Double.parseDouble(arrive.getEndNode().getProperty("lon").toString());
				Coordinate cDeest = new Coordinate(latDest, lonDest, false);
				
				Double latDest1 = Double.parseDouble(depart.getEndNode().getProperty("lat").toString());
				Double lonDest1 = Double.parseDouble(depart.getEndNode().getProperty("lon").toString());
				Coordinate cDeest1 = new Coordinate(latDest1, lonDest1, false);
				
				coordinates.add(cArrive);
				coordinates.add(cDeest);
				coordinates.add(cDeest1);
			} else {
				Double latArrive = Double.parseDouble(arrive.getStartNode().getProperty("lat").toString());
				Double lonArrive = Double.parseDouble(arrive.getStartNode().getProperty("lon").toString());
				Coordinate cArrive = new Coordinate(latArrive, lonArrive, false);
				
				Double latDest = Double.parseDouble(arrive.getEndNode().getProperty("lat").toString());
				Double lonDest = Double.parseDouble(arrive.getEndNode().getProperty("lon").toString());
				Coordinate cDeest = new Coordinate(latDest, lonDest, false);
				
				Double latDest1 = Double.parseDouble(depart.getEndNode().getProperty("lat").toString());
				Double lonDest1 = Double.parseDouble(depart.getEndNode().getProperty("lon").toString());
				Coordinate cDeest1 = new Coordinate(latDest1, lonDest1, true);
				
				coordinates.add(cArrive);
				coordinates.add(cDeest);
				coordinates.add(cDeest1);
			}
		}
		return coordinates;
	}

	private static Double calculateTotalDistance(Path m) {
		Double totalDistanceCost = 0.0;
		for (Relationship r : m.relationships()) {
			totalDistanceCost += (Double) r.getProperty("distance");
		}
		return totalDistanceCost;
	}

	private static Double calculateComfortIndex(Path m) {
		Double totalComfortCost = 1D;
		boolean isFirst = true;
		boolean eveningConvinience = false;
		for (Relationship r : m.relationships()) {
			if (isFirst) {
				String depTime = (String) r.getProperty("trainDeparture");

				String check1 = "17:00";
				String check2 = "23:59";
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				try {
					Date date1 = format.parse(depTime);

					Date cd1 = format.parse(check1);
					Date cd2 = format.parse(check2);

//					if (date1.after(cd1) && date1.before(cd2)) {
//						eveningConvinience = true;
//					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			totalComfortCost *= (Double) r.getProperty("trainComfortRating");
		}
		// as the number of trains increase, the comfort reduces.
		int noTrains = calculateNoOfTrain(m);

		double comformCost = totalComfortCost / noTrains;
		if (eveningConvinience) {
			comformCost = comformCost + .2 * comformCost + (comformCost + .2 * comformCost)*.1;
		} else {
			comformCost = comformCost + .2 * comformCost;
		}
		return comformCost;
	}

	private static int calculateNoOfTrain(Path m) {
		Set<Integer> trainSet = new TreeSet<Integer>();
		for (Relationship r : m.relationships()) {
			trainSet.add((int) r.getProperty("trainNumber"));
		}
		return trainSet.size();
	}

	private static long calculateDuration(Path m) throws ParseException {

		long timeTakenInMillis = 0;
		for (Relationship r : m.relationships()) {
			String startTimeString = (String) r.getProperty("trainDeparture");
			String endTimeString = (String) r.getProperty("trainArrival");
			int i = 0;
			startTimeString = startTimeString.replace(":", "");
			endTimeString = endTimeString.replace(":", "");
			if (Integer.parseInt(startTimeString) > Integer.parseInt(endTimeString)) {
				String tmp = startTimeString;
				startTimeString = endTimeString;
				endTimeString = tmp;
				i++;
			}

			SimpleDateFormat format = new SimpleDateFormat("HHmm");
			Date date1 = format.parse(startTimeString);
			Date date2 = format.parse(endTimeString);
			long ms = 0;
			if (i == 1) {
				ms = DAY - (date2.getTime() - date1.getTime());
			} else {
				ms = date2.getTime() - date1.getTime();
			}

			timeTakenInMillis += ms;

		}

		return timeTakenInMillis;
	}

	private static StringBuffer calculateRealTime(long ms) {

		StringBuffer text = new StringBuffer("");
		if (ms > DAY) {
			text.append(ms / DAY).append(" days ");
			ms %= DAY;
		}
		if (ms > HOUR) {
			text.append(ms / HOUR).append(" hours ");
			ms %= HOUR;
		}
		if (ms > MINUTE) {
			text.append(ms / MINUTE).append(" minutes ");
			ms %= MINUTE;
		}
		if (ms > SECOND) {
			text.append(ms / SECOND).append(" seconds ");
			ms %= SECOND;
		}
		text.append(ms + " secs");

		return text;
	}

	private static Map sortByComparator(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private static Map sortByComparatorReverse(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, Collections.reverseOrder(new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		}));

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		if (list.size() > 5) {
			list = list.subList(0, 4);
		}
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private static Map sortByComparatorForTime(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), calculateRealTime((long) entry.getValue()));
		}
		return sortedMap;
	}

	public static void printMap(Map<String, Double> map) {
		for (Map.Entry entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
	}

	public static void printTimeMap(Map<String, Long> map) {
		for (Map.Entry entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + calculateRealTime((long) entry.getValue()));
		}
	}

	public Path shortestPath(SearchCriteria searchCriteria, Results results) throws ParseException {

		Transaction tx = graphDb.beginTx();
		RailwayStation BLR_SBC = null, BLR_CANTT = null, SALEM = null, CHENNAI = null, DEL = null, EKM = null, MV = null;
		try {
			// BNC, SBC, SA, MAS, DLI, ERS, MV
			BLR_CANTT = new RailwayStation(graphDb, "Bangalore Cantt", "Karnataka", "BNC", 12.9921351,
					77.60056850000001);
			BLR_SBC = new RailwayStation(graphDb, "Bangalore City", "Karnataka", "SBC", 12.976549900000000000,
					77.568811500000040000);
			SALEM = new RailwayStation(graphDb, "Salem", "TamilNadu", "SA", 11.664325000000000000,
					78.146014199999970000);
			CHENNAI = new RailwayStation(graphDb, "Chennai", "TamilNadu", "MAS", 13.060422000000000000,
					80.249583000000030000);
			DEL = new RailwayStation(graphDb, "Delhi", "Delhi", "DLI", 28.635308000000000000, 77.224960000000010000);
			EKM = new RailwayStation(graphDb, "Eranakulam", "Kerala", "ERS", 9.970101699999999000,
					76.291173999999960000);
			MV = new RailwayStation(graphDb, "Mailadudurai", "Tamil Nadu", "MV", 11.094908200000000000,
					79.628278700000010000);

			// /**
			// * BLR_SBC to BLR_CANTT - Eranakulam Express
			// */
			Relationship BLR_SBC2BLR_CANTT_EE = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_EE.setProperty("trainName", "Eranakulam Express");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainNumber", 12677);
			BLR_SBC2BLR_CANTT_EE.setProperty("trainDeparture", "06:15");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainArrival", "06:25");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainComfortRating", .5d);
			BLR_SBC2BLR_CANTT_EE.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_EE.setProperty("distance", 5D);
			// /**
			// * BLR_CANTT to SALEM - Eranakulam Exp
			// */
			Relationship BLR_CANTT2SA = BLR_CANTT.createRailRouteTo(SALEM);
			BLR_CANTT2SA.setProperty("trainName", "Eranakulam Express");
			BLR_CANTT2SA.setProperty("trainNumber", 12677);
			BLR_CANTT2SA.setProperty("trainDeparture", "06:15");// correct time
																// TODO
			BLR_CANTT2SA.setProperty("trainArrival", "10:02");
			BLR_CANTT2SA.setProperty("trainComfortRating", .5d);
			BLR_CANTT2SA.setProperty("cost", 213d);
			BLR_CANTT2SA.setProperty("distance", 213d);

			/**
			 * SALEM to Eranakulam - Eranakulam Exp
			 */
			Relationship SA2EKM = SALEM.createRailRouteTo(EKM);
			SA2EKM.setProperty("trainName", "Eranakulam Express");
			SA2EKM.setProperty("trainNumber", 12677);
			SA2EKM.setProperty("trainDeparture", "10:05");
			SA2EKM.setProperty("trainArrival", "16:55");
			SA2EKM.setProperty("trainComfortRating", .5d);
			SA2EKM.setProperty("cost", 369d);
			SA2EKM.setProperty("distance", 369d);

			// /**
			// * BLR_SBC to BLR_CANTT - Mailadudurai Express
			// */
			Relationship BLR_SBC2BLR_CANTT_ME = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_ME.setProperty("trainName", "Mailadudurai Express");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainNumber", 16232);
			BLR_SBC2BLR_CANTT_ME.setProperty("trainDeparture", "18:45");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainArrival", "19:00");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainComfortRating", .5d);
			BLR_SBC2BLR_CANTT_ME.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_ME.setProperty("distance", 5D);

			/**
			 * // * BLR_CANTT to SALEM - Mailadudurai Exp
			 */
			Relationship BLR_CANTT2SA_MAILADU = BLR_CANTT.createRailRouteTo(SALEM);
			BLR_CANTT2SA_MAILADU.setProperty("trainName", "Mailadudurai Express");
			BLR_CANTT2SA_MAILADU.setProperty("trainNumber", 16232);
			BLR_CANTT2SA_MAILADU.setProperty("trainDeparture", "19:05");
			BLR_CANTT2SA_MAILADU.setProperty("trainArrival", "23:42");
			BLR_CANTT2SA_MAILADU.setProperty("trainComfortRating", .5d);
			BLR_CANTT2SA_MAILADU.setProperty("cost", 214d);
			BLR_CANTT2SA_MAILADU.setProperty("distance", 214d);

			/**
			 * // * SALEM to Mailadudurai - Mailadudurai Exp //
			 */
			Relationship SA2TJV_MAILADU = SALEM.createRailRouteTo(MV);
			SA2TJV_MAILADU.setProperty("trainName", "Mailadudurai Express");
			SA2TJV_MAILADU.setProperty("trainNumber", 16232);
			SA2TJV_MAILADU.setProperty("trainDeparture", "23:45");
			SA2TJV_MAILADU.setProperty("trainArrival", "07:00");
			SA2TJV_MAILADU.setProperty("trainComfortRating", .5d);
			SA2TJV_MAILADU.setProperty("cost", 323d);
			SA2TJV_MAILADU.setProperty("distance", 323d);

			/**
			 * BLR_SBC to BLR_CANTT - KARNATAKA EXP
			 */
			Relationship BLR_SBC2BLR_CANTT_KE = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_KE.setProperty("trainName", "KARNATAKA EXP");
			BLR_SBC2BLR_CANTT_KE.setProperty("trainNumber", 12627);
			BLR_SBC2BLR_CANTT_KE.setProperty("trainDeparture", "19:20");
			BLR_SBC2BLR_CANTT_KE.setProperty("trainArrival", "19:30");
			BLR_SBC2BLR_CANTT_KE.setProperty("trainComfortRating", .5d);
			BLR_SBC2BLR_CANTT_KE.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_KE.setProperty("distance", 5D);

			/**
			 * BLR_CANTT to Delhi - KARNATAKA EXP
			 */
			Relationship BLR_CANTT2DEL = BLR_CANTT.createRailRouteTo(DEL);
			BLR_CANTT2DEL.setProperty("trainName", "KARNATAKA EXP");
			BLR_CANTT2DEL.setProperty("trainNumber", 12627);
			BLR_CANTT2DEL.setProperty("trainDeparture", "19:32");
			BLR_CANTT2DEL.setProperty("trainArrival", "10:30");
			BLR_CANTT2DEL.setProperty("trainComfortRating", .5d);
			BLR_CANTT2DEL.setProperty("cost", 2401d);
			BLR_CANTT2DEL.setProperty("distance", 2401d);

			/**
			 * BLR_SBC to BLR_CANTT - LALBAGH EXP
			 */
			Relationship BLR_SBC2BLR_CANTT_LE = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_LE.setProperty("trainName", "LALBAGH EXP");
			BLR_SBC2BLR_CANTT_LE.setProperty("trainNumber", 12608);
			BLR_SBC2BLR_CANTT_LE.setProperty("trainDeparture", "06:30");
			BLR_SBC2BLR_CANTT_LE.setProperty("trainArrival", "06:40");
			BLR_SBC2BLR_CANTT_LE.setProperty("trainComfortRating", .5d);
			BLR_SBC2BLR_CANTT_LE.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_LE.setProperty("distance", 5D);

			/**
			 * BLR_CANTT to Chennai - LALBAGH EXP
			 */
			Relationship BLR_CANTT2SA_LALBAGH = BLR_CANTT.createRailRouteTo(CHENNAI);
			BLR_CANTT2SA_LALBAGH.setProperty("trainName", "LALBAGH EXP");
			BLR_CANTT2SA_LALBAGH.setProperty("trainNumber", 12608);
			BLR_CANTT2SA_LALBAGH.setProperty("trainDeparture", "06:42");
			BLR_CANTT2SA_LALBAGH.setProperty("trainArrival", "12:15");
			BLR_CANTT2SA_LALBAGH.setProperty("trainComfortRating", .5d);
			BLR_CANTT2SA_LALBAGH.setProperty("cost", 357d);
			BLR_CANTT2SA_LALBAGH.setProperty("distance", 357d);

			/**
			 * CHENNAI to SALEM - TRIVANDRUM MAIL
			 */
			Relationship CHENNAI2SA = CHENNAI.createRailRouteTo(SALEM);
			CHENNAI2SA.setProperty("trainName", "TRIVANDRUM MAIL");
			CHENNAI2SA.setProperty("trainNumber", 12623);
			CHENNAI2SA.setProperty("trainDeparture", "19:45");
			CHENNAI2SA.setProperty("trainArrival", "00:05");
			CHENNAI2SA.setProperty("trainComfortRating", .5d);
			CHENNAI2SA.setProperty("cost", 334d);
			CHENNAI2SA.setProperty("distance", 334d);

			/**
			 * SALEM to Eranakulam - TRIVANDRUM MAIL
			 */
			Relationship SA2EKM_TM = SALEM.createRailRouteTo(EKM);
			SA2EKM_TM.setProperty("trainName", "TRIVANDRUM MAIL");
			SA2EKM_TM.setProperty("trainNumber", 12623);
			SA2EKM_TM.setProperty("trainDeparture", "00:10");
			SA2EKM_TM.setProperty("trainArrival", "06:20");
			SA2EKM_TM.setProperty("trainComfortRating", .5d);
			SA2EKM_TM.setProperty("cost", 687d - 334d);
			SA2EKM_TM.setProperty("distance", 687d - 334d);
			tx.success();
		} finally {
			tx.finish();
		}

		Transaction tx2 = graphDb.beginTx();
		Map<String, Double> sortBasedOnCost = new LinkedHashMap<String, Double>();
		Map<String, Long> sortBasedOnTime = new LinkedHashMap<String, Long>();
		Map<String, Double> sortBasedOnComfort = new LinkedHashMap<String, Double>();
		Map<String, List<Coordinate>> coordinateListOfPath = new LinkedHashMap<String, List<Coordinate>>();

		Map<String, Double> sortBasedOnCost1 = new LinkedHashMap<String, Double>();
		Map<String, String> sortBasedOnTime1 = new LinkedHashMap<String, String>();
		Map<String, Double> sortBasedOnComfort1 = new LinkedHashMap<String, Double>();
		try {

			Expander relExpander = Traversal.expanderForTypes(RelationshipTypes.RAIL_ROUTE, Direction.OUTGOING);
			relExpander.add(RelationshipTypes.RAIL_ROUTE, Direction.OUTGOING);
			PathFinder<WeightedPath> sp = GraphAlgoFactory.aStar(relExpander, costEval, estimateEval);
			Path path = sp.findSinglePath(searchCriteria.getSource(), searchCriteria.getDestination());
			RailwayStation convenientStation = BLR_CANTT;
			path = sp.findSinglePath(convenientStation.getUnderlyingNode(), EKM.getUnderlyingNode());
			StringBuffer proposedPath1 = new StringBuffer();
			int i = 0;
			Relationship last = null;
			for (Relationship r1 : path.relationships()) {
				if (i == 0) {
					proposedPath1.append("BOARD ");
					proposedPath1.append(r1.getProperty("trainName"));
					proposedPath1.append(" AT ");
					proposedPath1.append(r1.getStartNode().getProperty("name"));
					/*
					 * proposedPath1.append("("+r1.getProperty("trainDeparture")+
					 * ")");
					 */
					proposedPath1.append(" TO ");
				}
				i++;
				last = r1;
			}

			proposedPath1.append(last.getEndNode().getProperty("name"));
			/* proposedPath1.append("("+last.getProperty("trainArrival")+")"); */
			proposedPath1.append("<BR>");

			Double singlePathTotalCost = calculateCost(path);
			Double singlePathTotalDistance = calculateTotalDistance(path);
			Double singlePathTotalComfortIndex = calculateComfortIndex(path);
			long singlePathDuration = calculateDuration(path);

			StringBuffer singlePathActualTime = calculateRealTime(singlePathDuration);
			results.setShortestPath(proposedPath1.toString());
			results.setShortestPathCost(singlePathTotalCost.toString());
			results.setShortestPathDuration(calculateRealTime(singlePathDuration).toString());
			results.setShortestPathComfort(singlePathTotalComfortIndex.toString());

			PathFinder<Path> p2 = GraphAlgoFactory.allSimplePaths(relExpander, 100);
			// Iterable<Path> mm = p2.findAllPaths(searchCriteria.getSource(),
			// searchCriteria.getDestination());
			Iterable<Path> mm = p2.findAllPaths(searchCriteria.getSource(), searchCriteria.getDestination());
			for (Path m : mm) {
				boolean isValid = filter(m);
				if (isValid) {

					StringBuffer proposedPath = new StringBuffer();
					String prevTrainName = null;
					for (Relationship r : m.relationships()) {
						String trainName = r.getProperty("trainName").toString();
						if (!trainName.equals(prevTrainName) ) {
							prevTrainName = trainName;
							proposedPath.append("<br>BOARD ");
						}
						
						proposedPath.append(trainName);
						proposedPath.append(" AT ");
						proposedPath.append(r.getStartNode().getProperty("name"));
						proposedPath.append("(" + r.getProperty("trainDeparture") + ")");
						
						proposedPath.append(" TO ");
						proposedPath.append(r.getEndNode().getProperty("name"));
						proposedPath.append("(" + r.getProperty("trainArrival") + ")");
						proposedPath.append("<BR>");
					}

					Double totalTotalCost = calculateCost(m);
					Double totalTotalDistance = calculateTotalDistance(m);
					Double totalTotalComfortIndex = calculateComfortIndex(m);
					long duration = calculateDuration(m);
					int trainns = calculateNoOfTrain(m);
					StringBuffer actualTime = calculateRealTime(duration);
					List<Coordinate> cord = calculateCoordinates(m);

					sortBasedOnCost.put(proposedPath.toString(), totalTotalCost);
					sortBasedOnTime.put(proposedPath.toString(), duration);
					sortBasedOnComfort.put(proposedPath.toString(), totalTotalComfortIndex);
					coordinateListOfPath.put(proposedPath.toString(), cord);
				}
			}
			results.setComfortMap(sortByComparatorReverse(sortBasedOnComfort));
			results.setTimeMap(sortByComparatorForTime(sortBasedOnTime));
			results.setCostMap(sortByComparator(sortBasedOnCost));
//			results.setCoordinateMap(coordinateListOfPath);
			

			tx2.success();
			return path;
		} finally {
			tx2.finish();
		}
	}

	private boolean filter(Path p) {
		
		boolean returnSomething = false;
		Iterable<Relationship> r = p.relationships();
		Iterator<Relationship> iterator = r.iterator();
		System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
		Relationship arrive = null;
		Relationship depart = null;
		boolean isFirst = true;
		Relationship previousRelationShip;
		List<Relationship> relationshipList = new ArrayList<Relationship>();
		for (Relationship r1 : p.relationships()) {
			relationshipList.add(r1);
		}
		for (int i = 0; i < relationshipList.size() - 1; i++) {
			arrive = relationshipList.get(i);
			depart = relationshipList.get(i+1);
			String arriveTrainNumber = arrive.getProperty("trainNumber").toString();
			String departTrainNumber = depart.getProperty("trainNumber").toString();
			System.out.println("============================");
			System.out.println(arrive.getStartNode().getProperty("code") + ">" + arrive.getEndNode().getProperty("code") + " TR:" + arrive.getProperty("trainName"));
			System.out.println(depart.getStartNode().getProperty("code") + ">" + depart.getEndNode().getProperty("code") + " TR:" + depart.getProperty("trainName"));
//			// train change
			if (!arriveTrainNumber.equals(departTrainNumber)) {
				String arriveTimeString = arrive.getProperty("trainArrival").toString();
				String departureTimeString = depart.getProperty("trainDeparture").toString();
				LocalTime arriveTime = LocalTime.parse(arriveTimeString);
				LocalTime departureTime = LocalTime.parse(departureTimeString);
				System.out.println("BOARDING PERIOD:" + arriveTimeString + "-" + departureTimeString + ":waiting:" + Minutes.minutesBetween(arriveTime, departureTime).getMinutes());
//				if (Minutes.minutesBetween(arriveTime, departureTime).getMinutes() > 360) {
//					return false;
//				}
				if (departureTime.isAfter(arriveTime) ) {
					returnSomething = true;
				} else {
					
					if (arriveTime.isAfter(LocalTime.parse("20:00"))
							&& departureTime.isBefore(LocalTime.parse("02:00"))) {
						returnSomething = true;
					}

				}
			}
		}
			
		return returnSomething;
	}

	public static GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	public static void setGraphDb(GraphDatabaseService graphDb) {
		AStarRouting.graphDb = graphDb;
	}
}
