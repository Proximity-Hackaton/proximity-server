package ch.proximity.proximityserver.suipoller;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventPollerImpl implements EventPoller, Runnable {
    private final static long SLEEP_TIME = 1000*10; // 10 seconds

    private final List<Consumer<List<ProximityNode>>> nodeConsumers = new LinkedList<>();
    private final List<Consumer<List<ProximityEdge>>> edgeConsumers = new LinkedList<>();
    private final Thread thread = new Thread(this);
    private boolean started = false;
    private boolean paused = false;

    @Override
    public void registerOnNewNodes(Consumer<List<ProximityNode>> nodeConsummer) {
        if(!started) {
            nodeConsumers.add(nodeConsummer);
        }
    }

    @Override
    public void registerOnNewEdges(Consumer<List<ProximityEdge>> edgeConsummer) {
        if(!started) {
            edgeConsumers.add(edgeConsummer);
        }
    }

    @Override
    public void start() {
        started = true;
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
        started = false;
    }


    @Override
    public void run() {
        while(!thread.isInterrupted()) {

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
