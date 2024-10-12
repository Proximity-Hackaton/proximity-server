package ch.proximity.proximityserver.database;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.graph.SimpleDirectedGraph;

public interface DBReader {

    SimpleDirectedGraph<ProximityNode, ProximityEdge> BFS_n_read(
            String walletUUID, int depth, long minTimestamp, long maxTimestamp);

}
