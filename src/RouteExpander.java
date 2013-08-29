import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.Predicate;


public class RouteExpander implements Expander {

	@Override
	public Iterable<Relationship> expand(Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander add(RelationshipType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander add(RelationshipType arg0, Direction arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander addNodeFilter(Predicate<? super Node> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander addRelationshipFilter(Predicate<? super Relationship> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander addRelationsipFilter(Predicate<? super Relationship> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander remove(RelationshipType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expander reversed() {
		// TODO Auto-generated method stub
		return null;
	}

}
