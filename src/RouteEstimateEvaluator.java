import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphdb.Node;


public class RouteEstimateEvaluator implements EstimateEvaluator<Double> {

	@Override
	public Double getCost(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return 1d;
	}

}
