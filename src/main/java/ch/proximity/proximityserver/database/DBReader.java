package ch.proximity.database;

import ch.proximity.model.ProximityEdge;
import ch.proximity.model.ProximityNode;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public interface DBReader {

    SimpleDirectedGraph<ProximityNode, ProximityEdge> BFS_n_read(
            String walletUUID, int depth, long minTimestamp, long maxTimestamp);

}
