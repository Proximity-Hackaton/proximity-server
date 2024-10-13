package ch.proximity.proximityserver.explorer;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.Graph;

import java.util.List;

public interface TrustFunction {
    void applyFunction(Graph<ProximityNode, ProximityEdge> graph, List<ProximityNode> trustees, int k);
}
