package ch.proximity.proximityserver.suipoller;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface EventPoller {
    void registerOnNewNodes(Consumer<List<ProximityNode>> nodeConsummer);
    void registerOnNewEdges(Consumer<List<ProximityEdge>> edgeConsummer);
    void start();
    void stop();
    void pause();
}


