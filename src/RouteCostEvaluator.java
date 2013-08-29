import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;


public class RouteCostEvaluator implements CostEvaluator<Double> {

	@Override
	public Double getCost(Relationship arg0, Direction arg1) {
		return 1d;
	}

}
