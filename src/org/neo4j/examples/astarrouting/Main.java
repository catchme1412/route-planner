package org.neo4j.examples.astarrouting;
 
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.ReadableIndex;
 
public class Main {
 
    public static void main(String args[]){
        //GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "db/graphDB" );
        GraphDatabaseService graphDb = new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder( "/tmp/neo4j-db" ).
                setConfig( GraphDatabaseSettings.node_keys_indexable, "name" ).
                setConfig( GraphDatabaseSettings.relationship_keys_indexable, "name" ).
                setConfig( GraphDatabaseSettings.node_auto_indexing, "true" ).
                setConfig( GraphDatabaseSettings.relationship_auto_indexing, "true" ).
                newGraphDatabase();
 
        registerShutdownHook(graphDb);
 
        Relationship relation;
        Node node1;
        Node node2;
        Transaction tx;
 
        //build 20 nodes, 2 related nodes in each iteration
        for (int i = 0; i < 10;i++){
            tx = graphDb.beginTx();
 
            node1 = graphDb.createNode();
            //name:node-[1,2...10]-1
            node1.setProperty("name", "node-"+(i+1)+"-1");
            node2 = graphDb.createNode();
            //name:node-[1,2...10]-2
            node2.setProperty("name", "node-"+(i+1)+"-2");
 
            relation = node1.createRelationshipTo(node2, RelTypes.RELATE_TO);
            //name: relation: [1,2,..10]
            relation.setProperty("name", "relation: "+(i+1));
 
            tx.success();
            tx.finish();
 
        }
        ReadableIndex<Node> autoNodeIndex = graphDb.index()
                .getNodeAutoIndexer()
                .getAutoIndex();
        System.out.println(autoNodeIndex.getName());
 
    }
    public static enum RelTypes implements RelationshipType
    {
        RELATE_TO;
    }
 
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
 
            }
        } );
    }
 
}