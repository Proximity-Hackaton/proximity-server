package ch.proximity.proximityserver.database;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.SimpleDirectedGraph;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * DISCLAIMER : This is a mock implementation of a GraphDB, this is not supposed to run in production (obviously)
 * Please don't pay attention to the lack of efficiency of the methods in this class
 */
public class FakeDB implements DBReader, DBWriter {
    private final static int MAX_READERS = 10;
    private final Semaphore readSemaphore = new Semaphore(MAX_READERS);
    private final Semaphore writeSemaphore = new Semaphore(1);

    private final SimpleDirectedGraph<ProximityNode, ProximityEdge> graph;
    private final Map<String, ProximityNode> uuidToNode = new HashMap<>();

    private static FakeDB mainInstance;

    public FakeDB(){
        this.graph = new SimpleDirectedGraph<>(ProximityEdge.class);
    }

    private FakeDB(SimpleDirectedGraph<ProximityNode, ProximityEdge> graph){
        this.graph = graph;
    }

    /**
     * WARNING : THIS CODE SHOULD EXECUTE ON A GRAPH DATABASE
     * The implementation below is a Mock Implementation for proof of concept and is therefore not optimized
     * The state of the database is stored and queried in the RAM
     * ONLY VALID FOR PROOF OF CONCEPT. Not a real implementation.
     */
    @Override
    public SimpleDirectedGraph<ProximityNode, ProximityEdge> BFS_n_read(String uid, int depth, long minTimestamp, long maxTimestamp) {

        acquireReaderLock();
        SimpleDirectedGraph<ProximityNode, ProximityEdge> subGraph = new SimpleDirectedGraph<>(ProximityEdge.class);
        ProximityNode source = nodeFromUUID(uid);

        LinkedList<Pair<ProximityNode, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(source, 0));
        subGraph.addVertex(source);

        Pair<ProximityNode, Integer> current;
        while(!queue.isEmpty()){
            current = queue.pop();
            if(current.getSecond() > depth){
                break;
            }

            Set<ProximityEdge> outgoingEdges = graph.outgoingEdgesOf(current.getFirst());

            if(current.getSecond() == depth){
                for(ProximityEdge e : outgoingEdges){
                    if(e.getTimestamp() >= minTimestamp && e.getTimestamp() <= maxTimestamp){
                        ProximityNode target = graph.getEdgeTarget(e);
                        if(subGraph.containsVertex(target)){
                            subGraph.addEdge(current.getFirst(), target, e);
                        }
                    }
                }
            }else{
                for(ProximityEdge e : outgoingEdges){
                    if(e.getTimestamp() >= minTimestamp && e.getTimestamp() <= maxTimestamp){
                        ProximityNode target = graph.getEdgeTarget(e);
                        if(!subGraph.containsVertex(target)){
                            subGraph.addVertex(target);
                            queue.add(new Pair<>(target, current.getSecond()+1));
                        }
                        subGraph.addEdge(current.getFirst(), target, e);
                    }
                }
            }
        }


        releaseReaderLock(); // :)
        return subGraph;
    }

    private ProximityNode nodeFromUUID(String uid){
        return uuidToNode.get(uid);
    }

    private void acquireWriterLock(){
        try {
            writeSemaphore.acquire();
            readSemaphore.acquire(MAX_READERS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void releaseWriterLock(){
        readSemaphore.release(MAX_READERS);
        writeSemaphore.release();
    }

    private void acquireReaderLock(){
        try {
            writeSemaphore.acquire();
            writeSemaphore.release();
            readSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void releaseReaderLock(){
        readSemaphore.release();
    }

    @Override
    public void addEdges(List<ProximityEdge> edges){
        acquireWriterLock();
        edges.stream().forEach((e) -> graph.addEdge(
                uuidToNode.get(e.getNodeSourceUUID()), uuidToNode.get(e.getNodeDestUUID()), e
        ));
        releaseWriterLock();
    }

    @Override
    public void addNodes(List<ProximityNode> nodes){
        acquireWriterLock();
        nodes.stream().forEach((n) -> {
            uuidToNode.put(n.getNodeUUID(), n);
            graph.addVertex(n);
        });
        releaseWriterLock();

    }

    public static FakeDB getInstance(){
        return mainInstance;
    }

    public static FakeDB fakeInstance(){
        ProximityNode[] nodes = {
                new ProximityNode("1", "a1a"),
                new ProximityNode("2", "a2a"),
                new ProximityNode("3", "a3a"),
                new ProximityNode("4", "a4a"),
                new ProximityNode("5", "a5a"),
                new ProximityNode("6", "a6a"),
                new ProximityNode("7", "a7a"),
                new ProximityNode("8", "a8a"),
                new ProximityNode("9", "a9a"),
                new ProximityNode("10", "a10a"),
                new ProximityNode("11", "a11a"),
                new ProximityNode("12", "a12a"),
                new ProximityNode("13", "a13a"),
                new ProximityNode("14", "a14a"),
        };

        ProximityEdge[] edges = {
                new ProximityEdge("a", 20, "1", "2"),
                new ProximityEdge("b", 20, "2", "1"),
                new ProximityEdge("c", 20, "2", "3"),
                new ProximityEdge("d", 20, "3", "2"),
                new ProximityEdge("e", 30, "3", "14"),
                new ProximityEdge("f", 30, "14", "3"),
                new ProximityEdge("g", 30, "4", "14"),
                new ProximityEdge("h", 30, "14", "4"),
                new ProximityEdge("i", 40, "5", "14"),
                new ProximityEdge("j", 40, "6", "14"),
                new ProximityEdge("k", 40, "14", "6"),
                new ProximityEdge("l", 40, "6", "7"),
                new ProximityEdge("m", 50, "7", "6"),
                new ProximityEdge("n", 50, "7", "9"),
                new ProximityEdge("o", 50, "9", "7"),
                new ProximityEdge("p", 50, "9", "10"),
                new ProximityEdge("q", 60, "10", "9"),
                new ProximityEdge("r", 60, "11", "14"),
                new ProximityEdge("s", 60, "14", "11"),
                new ProximityEdge("t", 60, "11", "12"),
                new ProximityEdge("u", 60, "12", "11"),
                new ProximityEdge("v", 60, "12", "13"),
                new ProximityEdge("w", 60, "13", "12"),
                new ProximityEdge("w", 70, "14", "9"),
                new ProximityEdge("w", 70, "9", "14"),
                new ProximityEdge("w", 70, "6", "9"),
                new ProximityEdge("w", 70, "9", "6"),



        };

        SimpleDirectedGraph<ProximityNode, ProximityEdge> fakeGraph = new SimpleDirectedGraph<>(ProximityEdge.class);

        FakeDB db = new FakeDB();
        db.addNodes(List.of(nodes));
        db.addEdges(List.of(edges));

        return db;
    }

}
