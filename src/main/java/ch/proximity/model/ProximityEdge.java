package ch.proximity.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class ProximityEdge extends DefaultWeightedEdge {
    private long timestamp;

    public ProximityEdge(long timestamp) {
        super();
        this.timestamp = timestamp;
    }
}
