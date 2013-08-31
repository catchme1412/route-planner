package org.neo4j.examples.astarrouting;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

public class RouteCostEvaluator implements CostEvaluator {

	@Override
	public Object getCost(Relationship arg0, Direction arg1) {
		// TODO Auto-generated method stub
		return 1d;
	}

}
