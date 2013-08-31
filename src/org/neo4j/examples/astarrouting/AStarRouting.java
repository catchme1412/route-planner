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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class AStarRouting {
	private static final EstimateEvaluator<Double> estimateEval = CommonEvaluators.geoEstimateEvaluator(
			RailwayStation.LATITUDE, RailwayStation.LONGITUDE);
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	private static Map<String,Double> sortBasedOnCost = new LinkedHashMap<String,Double>();
	private static Map<String,Long> sortBasedOnTime = new LinkedHashMap<String,Long>();
	
	// TODO create a custom cost evaluator
	private static final CostEvaluator<Double> costEval = new RouteCostEvaluator();//CommonEvaluators.doubleCostEvaluator(RailwayStation.COST);

	public static void main(final String[] args) throws ParseException {
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase("/tmp/neo4j-db");
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
	 * @throws ParseException 
	 */
	private static void routing(final GraphDatabaseService graphDb) throws ParseException {
		Transaction tx = graphDb.beginTx();
		RailwayStation BLR_SBC = null, BLR_CANTT = null, SALEM = null, CHENNAI = null, DEL = null, EKM = null, MV = null;
		try {
			
			BLR_CANTT = new RailwayStation(graphDb, "Bangalore Cantt", "Karnataka");
			BLR_SBC = new RailwayStation(graphDb, "Bangalore City", "Karnataka");
			SALEM = new RailwayStation(graphDb, "Salem", "TamilNadu");
			CHENNAI = new RailwayStation(graphDb, "Chennai", "TamilNadu");
			DEL = new RailwayStation(graphDb, "Delhi", "Delhi");
			EKM = new RailwayStation(graphDb, "Eranakulam", "Kerala");
			MV = new RailwayStation(graphDb, "Mailadudurai", "Tamil Nadu");

			
			
//			/**
//			 * BLR_SBC to BLR_CANTT - Eranakulam Express
//			 */
			Relationship BLR_SBC2BLR_CANTT_EE = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_EE.setProperty("trainName", "Eranakulam Express");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainNumber", 12677);
			BLR_SBC2BLR_CANTT_EE.setProperty("trainDeparture", "06:15");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainArrival", "06:25");
			BLR_SBC2BLR_CANTT_EE.setProperty("trainComfortRating", 4.5D);
			BLR_SBC2BLR_CANTT_EE.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_EE.setProperty("distance", 5D);
//			/**
//			 * BLR_CANTT to SALEM - Eranakulam Exp
//			 */
			Relationship BLR_CANTT2SA = BLR_CANTT.createRailRouteTo(SALEM);
			BLR_CANTT2SA.setProperty("trainName", "Eranakulam Express");
			BLR_CANTT2SA.setProperty("trainNumber", 12677);
			BLR_CANTT2SA.setProperty("trainDeparture", "06:15");//correct time TODO
			BLR_CANTT2SA.setProperty("trainArrival", "10:02");
			BLR_CANTT2SA.setProperty("trainComfortRating", 4.5D);
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
			SA2EKM.setProperty("trainComfortRating", 4.5D);
			SA2EKM.setProperty("cost", 369d);
			SA2EKM.setProperty("distance", 369d);

			//			/**
//			 * BLR_SBC to BLR_CANTT - Mailadudurai Express
//			 */
			Relationship BLR_SBC2BLR_CANTT_ME = BLR_SBC.createRailRouteTo(BLR_CANTT);
			BLR_SBC2BLR_CANTT_ME.setProperty("trainName", "Mailadudurai Express");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainNumber", 16232);
			BLR_SBC2BLR_CANTT_ME.setProperty("trainDeparture", "06:15");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainArrival", "06:25");
			BLR_SBC2BLR_CANTT_ME.setProperty("trainComfortRating", 4.5D);
			BLR_SBC2BLR_CANTT_ME.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_ME.setProperty("distance", 5D);
			
			/**
//			 * BLR_CANTT to SALEM - Mailadudurai Exp
//			 */
			Relationship BLR_CANTT2SA_MAILADU = BLR_CANTT.createRailRouteTo(SALEM);
			BLR_CANTT2SA_MAILADU.setProperty("trainName", "Mailadudurai Express");
			BLR_CANTT2SA_MAILADU.setProperty("trainNumber", 16232);
			BLR_CANTT2SA_MAILADU.setProperty("trainDeparture", "19:05");
			BLR_CANTT2SA_MAILADU.setProperty("trainArrival", "23:42");
			BLR_CANTT2SA_MAILADU.setProperty("trainComfortRating", 4.5D);
			BLR_CANTT2SA_MAILADU.setProperty("cost", 214d);
			BLR_CANTT2SA_MAILADU.setProperty("distance", 214d);
			
			
			/**
//			 * SALEM to Mailadudurai - Mailadudurai Exp
//			 */
			Relationship SA2TJV_MAILADU = SALEM.createRailRouteTo(MV);
			SA2TJV_MAILADU.setProperty("trainName", "Mailadudurai Express");
			SA2TJV_MAILADU.setProperty("trainNumber", 16232);
			SA2TJV_MAILADU.setProperty("trainDeparture", "23:45");
			SA2TJV_MAILADU.setProperty("trainArrival", "07:00");
			SA2TJV_MAILADU.setProperty("trainComfortRating", 4.5D);
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
			BLR_SBC2BLR_CANTT_KE.setProperty("trainComfortRating", 4.5D);
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
			BLR_CANTT2DEL.setProperty("trainComfortRating", 4.5D);
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
			BLR_SBC2BLR_CANTT_LE.setProperty("trainComfortRating", 4.5D);
			BLR_SBC2BLR_CANTT_LE.setProperty("cost", 5D);
			BLR_SBC2BLR_CANTT_LE.setProperty("distance", 5D);
			
			
			
			/**
			 * BLR_CANTT to Chennai - LALBAGH EXP
			 */
			Relationship BLR_CANTT2SA_LALBAGH = BLR_CANTT.createRailRouteTo(SALEM);
			BLR_CANTT2SA_LALBAGH.setProperty("trainName", "LALBAGH EXP");
			BLR_CANTT2SA_LALBAGH.setProperty("trainNumber", 12608);
			BLR_CANTT2SA_LALBAGH.setProperty("trainDeparture", "06:42");
			BLR_CANTT2SA_LALBAGH.setProperty("trainArrival", "12:15");
			BLR_CANTT2SA_LALBAGH.setProperty("trainComfortRating", 4.5D);
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
			CHENNAI2SA.setProperty("trainComfortRating", 4.5D);
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
			SA2EKM_TM.setProperty("trainComfortRating", 4.5D);
			SA2EKM_TM.setProperty("cost", 687d-334d);
			SA2EKM_TM.setProperty("distance", 687d-334d);
			tx.success();
		} finally {
			tx.finish();
		}
		Transaction tx2 = graphDb.beginTx();
		try {
			Expander relExpander = Traversal.expanderForTypes(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
			relExpander.add(RelationshipTypes.RAIL_ROUTE, Direction.BOTH);
			PathFinder<WeightedPath> sp = GraphAlgoFactory.aStar(relExpander, costEval, estimateEval);
			Path path = sp.findSinglePath(BLR_SBC.getUnderlyingNode(), EKM.getUnderlyingNode());
			System.out.println("SHORTEST PATH IS GIVEN BELOW:");
			for (Node node : path.nodes()) {
				System.out.print("=>" + node.getProperty(RailwayStation.NAME));
			}

			Iterable<WeightedPath> allPaths = sp.findAllPaths(BLR_SBC.getUnderlyingNode(), EKM.getUnderlyingNode());
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
			Iterable<Path> mm = p2.findAllPaths(BLR_SBC.getUnderlyingNode(), EKM.getUnderlyingNode());
			for (Path m : mm) {
				/*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");*/
				StringBuffer proposedPath = new StringBuffer();
//				System.out.print("\nBLR==>");
				for (Relationship r :m.relationships()) {
					proposedPath.append("==>");
					proposedPath.append(r.getProperty("trainName"));
					proposedPath.append("::::");
					proposedPath.append(r.getStartNode().getProperty("name"));
					proposedPath.append("---");
					proposedPath.append(r.getEndNode().getProperty("name"));
					/*System.out.println("==>" + r.getProperty("trainName") + "::::" +r.getStartNode().getProperty("name") + "---" +r.getEndNode().getProperty("name") );*/
				}
				
				
				Double totalTotalCost = calculateCost(m);
				Double totalTotalDistance = calculateTotalDistance(m);
				Double totalTotalComfortIndex = calculateComfortIndex(m);
				long duration = calculateDuration(m);
				int trainns = calculateNoOfTrain(m);
				
				StringBuffer actualTime = calculateRealTime(duration);

				sortBasedOnCost.put(proposedPath.toString(), totalTotalCost);
				sortBasedOnTime.put(proposedPath.toString(), duration);
				
				
				
				/*System.out.println("Total Cost."+totalTotalCost);
				System.out.println("Distance."+totalTotalDistance);
				System.out.println("Comfort."+totalTotalComfortIndex);
				System.out.println("Duration"+actualTime);*/
				/*System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");*/
			}
			
			Map<String, Double> sortedCostMap = sortByComparator(sortBasedOnCost);
			Map<String, Long> sortedTimeMap = sortByComparator(sortBasedOnTime);
			System.out.println("======================================================");
			printMap(sortedCostMap);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");			
			printTimeMap(sortedTimeMap);
			System.out.println("======================================================");

			tx2.success();
		} finally {
			tx2.finish();
		}
	}
	
	private static Double calculateCost(Path m) {
		Double totalTimeCost = 0.0;
//		ArrayList<Double> costOfRoute = new ArrayList<Double>();
		for (Relationship r : m.relationships() ){
			totalTimeCost +=(Double)r.getProperty("cost");
		}
//		for (int i=0;i<costOfRoute.size();i++) {
//			totalTimeCost = totalTimeCost + costOfRoute.get(i);
//		}
		
		return totalTimeCost;
	}
	
	private static Double calculateTotalDistance(Path m) {
		Double totalDistanceCost = 0.0;
		for (Relationship r : m.relationships() ){
			totalDistanceCost += (Double)r.getProperty("distance");
		}
		return totalDistanceCost;
	}
	
	private static Double calculateComfortIndex(Path m) {
		Double totalComfortCost = 0.0;
		for (Relationship r : m.relationships() ){
			totalComfortCost += (Double)r.getProperty("trainComfortRating");
		}
		return totalComfortCost;
	}
	
	private static int calculateNoOfTrain(Path m) {
		Set<Integer> trainSet = new TreeSet<Integer>();
		for (Relationship r : m.relationships() ){
			trainSet.add((int)r.getProperty("trainNumber"));
		}
		return trainSet.size();
	}
	
	private static long calculateDuration(Path m) throws ParseException {
		
		long timeTakenInMillis = 0;
		for (Relationship r : m.relationships() ){
			String startTimeString = (String)r.getProperty("trainDeparture");
		    String endTimeString = (String)r.getProperty("trainArrival");
		    int i =0;
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
	
	private static StringBuffer calculateRealTime (long ms) {
		
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
	    text.append(ms + " ms");
	
	    return text;
	}
	
	private static Map sortByComparator(Map unsortMap) {
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
                                       .compareTo(((Map.Entry) (o2)).getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static void printMap(Map<String, Double> map){
		for (Map.Entry entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}
	}
	
	public static void printTimeMap(Map<String, Long> map){
		for (Map.Entry entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + calculateRealTime((long)entry.getValue()));
		}
	}
}


