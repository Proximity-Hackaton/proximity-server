package ch.proximity.proximityserver.database;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;

import java.util.List;

public interface DBWriter {
    void addEdges(List<ProximityEdge> edges);
    void addNodes(List<ProximityNode> nodes);
}
