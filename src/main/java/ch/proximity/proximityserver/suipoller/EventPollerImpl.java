package ch.proximity.proximityserver.suipoller;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;

import java.util.List;
import java.util.function.Consumer;

public class EventPollerImpl implements EventPoller {

    @Override
    public void registerOnNewNodes(Consumer<List<ProximityNode>> nodeConsummer) {

    }

    @Override
    public void registerOnNewEdges(Consumer<List<ProximityEdge>> edgeConsummer) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }
}
