package ch.proximity.proximityserver.suipoller;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    private List<ProximityNode> convertToNodes(List<JsonElement> elems){
        return elems.stream()
                .map((e) -> {
                    JsonObject parsedJson = e.getAsJsonObject().get("parsedJson").getAsJsonObject();
                    return new ProximityNode(parsedJson.get("owner").getAsString(), parsedJson.get("user").getAsString());
                }).toList();
    }



    private List<ProximityEdge> convertNodeUpdateToEdges(JsonElement elem){
        JsonObject parsedJson = elem.getAsJsonObject().get("parsedJson").getAsJsonObject();
        String userID = parsedJson.get("user").getAsString();
        JsonObject currentNode = parsedJson.get("current_node").getAsJsonObject();
        long timestamp = currentNode.get("timestamp").getAsLong();
        JsonArray neighbors = currentNode.get("neighbors").getAsJsonArray();
        List<ProximityEdge> edges = new ArrayList<>();
        for(JsonElement n : neighbors){
            String destID = n.getAsString();
            edges.add(new ProximityEdge(timestamp, userID, destID));
        }
        return edges;
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
