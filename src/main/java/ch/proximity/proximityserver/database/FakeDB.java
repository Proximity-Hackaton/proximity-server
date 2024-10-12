package ch.proximity.proximityserver.database;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public FakeDB(){
        this.graph = new SimpleDirectedGraph<>(ProximityEdge.class);
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

        Pair<ProximityNode, Integer> current;
        while(!queue.isEmpty()){
            current = queue.pop().first;
        }








        releaseReaderLock(); // :)
        return graph;
    }

    private ProximityNode nodeFromUUID(String uid){

        return null;
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

}
