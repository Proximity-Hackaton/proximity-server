package ch.proximity.database;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public interface DBReader {

    SimpleDirectedGraph<Object, Object> bfsReader(String uid, int depth, long minTimestamp, long maxTimestamp);

}
