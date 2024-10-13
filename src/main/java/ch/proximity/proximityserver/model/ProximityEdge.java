package ch.proximity.proximityserver.model;

import org.jgrapht.graph.DefaultEdge;

public class ProximityEdge extends DefaultEdge {
    private final long timestamp;
    private final String nodeSourceUUID;
    private final String nodeDestUUID;

    public ProximityEdge(long timestamp, String nodeSourceUUID, String nodeDestUUID) {
        super();
        this.timestamp = timestamp;
        this.nodeSourceUUID = nodeSourceUUID;
        this.nodeDestUUID = nodeDestUUID;
    }

    public String getNodeSourceUUID(){
        return nodeSourceUUID;
    }
    public String getNodeDestUUID(){
        return nodeDestUUID;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
