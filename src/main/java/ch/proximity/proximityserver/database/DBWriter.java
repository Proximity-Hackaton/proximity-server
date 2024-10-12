package ch.proximity.database;

import ch.proximity.model.ProximityEdge;
import ch.proximity.model.ProximityNode;

import java.util.List;

public interface DBWriter {
    void addEdges(List<ProximityEdge> edges);
    void addNodes(List<ProximityNode> nodes);
}
