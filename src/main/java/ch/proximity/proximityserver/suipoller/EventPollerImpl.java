package ch.proximity.proximityserver.suipoller;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class EventPollerImpl implements EventPoller, Runnable {
    private final static long SLEEP_TIME = 1000*10; // 10 seconds

    private final List<Consumer<List<ProximityNode>>> nodeConsumers = new LinkedList<>();
    private final List<Consumer<List<ProximityEdge>>> edgeConsumers = new LinkedList<>();
    private final Thread thread = new Thread(this);
    private boolean started = false;
    private final static String PACKAGE = "0x1f287b94919e183b17caff67e629b05257fc1517b6176c375c70d9f4cdbb6462";
    private final static String MODULE = "proximity";
    private final static String NEW_EDGES_EVENT = "NodeUpdateEvent";
    private final static String NEW_NODE_EVENT = "NewUserEvent";
    private MiniSUIJavaSDK.PagingCursor currentEdgePage = null;
    private MiniSUIJavaSDK.PagingCursor currentNodePage = null;
    private final MiniSUIJavaSDK sui = new MiniSUIJavaSDK();
    private final static int MAX_ITEMS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(EventPollerImpl.class);

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
                    return new ProximityNode(parsedJson.get("user").getAsString(), parsedJson.get("owner").getAsString());
                }).toList();
    }

    private List<ProximityEdge> convertToEdges(List<JsonElement> elems){
        List<ProximityEdge> edges = new ArrayList<>();
        for(JsonElement e : elems){
            edges.addAll(convertNodeUpdateToEdges(e));
        }
        return edges;
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

    private List<ProximityEdge> fetchNewEdges() throws MiniSUIJavaSDK.SUIRequestException {

        MiniSUIJavaSDK.Pair<MiniSUIJavaSDK.PagingCursor, List<JsonElement>> result =  sui.queryEvents(
                PACKAGE,
                MODULE,
                NEW_EDGES_EVENT,
                currentEdgePage,
                MAX_ITEMS
        );
        currentEdgePage = result.first();
        return convertToEdges(result.second());
    }

    private List<ProximityNode> fetchNewNodes() throws MiniSUIJavaSDK.SUIRequestException {
        System.out.println(currentNodePage);
        System.out.println(currentEdgePage);
        MiniSUIJavaSDK.Pair<MiniSUIJavaSDK.PagingCursor, List<JsonElement>> result =  sui.queryEvents(
                PACKAGE,
                MODULE,
                NEW_NODE_EVENT,
                currentNodePage,
                MAX_ITEMS
        );
        currentNodePage = result.first();
        return convertToNodes(result.second());
    }

    public void pollRound() {
        MiniSUIJavaSDK.PagingCursor oldNodePage = currentNodePage;
        MiniSUIJavaSDK.PagingCursor oldEdgePage = currentEdgePage;
        List<ProximityNode> nodes;
        List<ProximityEdge> edges;
        try {
            nodes = fetchNewNodes();
        } catch (MiniSUIJavaSDK.SUIRequestException e) {
            currentNodePage = oldNodePage;
            currentEdgePage = oldEdgePage;
            logger.warn("Last fetch Node Request issued an Exception. Could not read Blockchain!");
            System.out.println("Node DEFEAT");
            return; // don't fetch the edges
        }
        try {
            edges= fetchNewEdges();
        } catch (MiniSUIJavaSDK.SUIRequestException e) {
            currentNodePage = oldNodePage;
            currentEdgePage = oldEdgePage;
            System.out.println("EDGE DEFEAT");
            logger.warn("Last fetch Edge Request issued an Exception. Could not read Blockchain!");
            return;
        }

        nodes = Collections.unmodifiableList(nodes);
        edges = Collections.unmodifiableList(edges);
        for(Consumer<List<ProximityNode>> nodeConsumer : nodeConsumers){
            nodeConsumer.accept(nodes);
        }
        for(Consumer<List<ProximityEdge>> eC : edgeConsumers){
            eC.accept(edges);
        }
        System.out.println("sucesss");

    }

    @Override
    public void run() {
        System.out.println("Runned started");
        while(!thread.isInterrupted()) {
            System.out.println("Doing poll round");
            pollRound();
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                logger.warn("Poll thread interrupted while sleeping");
                Thread.currentThread().interrupt();
            }
        }
        logger.warn("Thread interrupted");
    }
}
