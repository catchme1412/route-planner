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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.Traversal;

public class DatabaseDriver {

	private GraphDatabaseService graphDb;

	private long firstId = 0;
	private long secondId = 0;

	public DatabaseDriver() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/graphdb");
		registerShutdownHook(graphDb);
	}

	public void loadData() {
		Transaction tx = graphDb.beginTx();
		try {
			Node firstNode = graphDb.createNode();
			firstNode.setProperty("message", "Hello, ");
			Node secondNode = graphDb.createNode();
			secondNode.setProperty("message", "World!");
			firstId = firstNode.getId();
			secondId = secondNode.getId();
			System.out.println(firstNode.getId());
			System.out.println(secondNode.getId());
			Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.ROUTE);
			relationship.setProperty("message", "brave Neo4j ");

			tx.success();
		} finally {
			tx.finish();
		}

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

	public void findShortestRoute() {
		// Expander expander = new RouteExpander();
		Expander relExpander = Traversal.expanderForTypes(RelTypes.STATION, Direction.BOTH);
		relExpander.add(RelTypes.STATION, Direction.BOTH);
		EstimateEvaluator<Double> estimateEval = CommonEvaluators.geoEstimateEvaluator("PROP_LATITUDE",
				"PROP_LONGITUDE");
		CostEvaluator<Double> lengthEvaluator = new RouteCostEvaluator();
		EstimateEvaluator<Double> estimateEvaluator = new RouteEstimateEvaluator();
		;
		Transaction tx = graphDb.beginTx();
		PathFinder<WeightedPath> result = null;
		try {
			result = GraphAlgoFactory.aStar(relExpander, lengthEvaluator, estimateEval);
			tx.success();
		} finally {
			tx.finish();
		}
		tx = graphDb.beginTx();
		try {
			System.out.println(result.findSinglePath(graphDb.getNodeById(firstId), graphDb.getNodeById(secondId)));
			tx.success();
		} finally {
			tx.finish();
		}
	}

}
