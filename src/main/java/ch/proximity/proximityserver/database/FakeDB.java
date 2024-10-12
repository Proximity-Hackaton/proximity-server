package ch.proximity.database;

import ch.proximity.model.ProximityEdge;
import ch.proximity.model.ProximityNode;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class FakeDB implements DBReader, DBWriter {
    private final static int MAX_READERS = 10;
    private final Semaphore readSemaphore = new Semaphore(MAX_READERS);
    private final Semaphore writeSemaphore = new Semaphore(1);

    private final SimpleDirectedGraph<ProximityNode, ProximityEdge> graph;
    private final Map<String, ProximityNode> uuidToNode = new HashMap<>();

    public FakeDB(){
        this.graph = new SimpleDirectedGraph<>(ProximityEdge.class);
    }

    @Override
    public SimpleDirectedGraph<ProximityNode, ProximityEdge> BFS_n_read(String uid, int depth, long minTimestamp, long maxTimestamp) {
        acquireReaderLock();
        ProximityNode source = nodeFromUUID(uid);
        BreadthFirstIterator<ProximityNode, ProximityEdge> iterator =
                new BreadthFirstIterator<>(graph, source);


        SimpleDirectedGraph<ProximityNode, ProximityEdge> subGraph = new SimpleDirectedGraph<>(ProximityEdge.class);
        subGraph.addVertex(source);

        ProximityNode currentNode = source;
        boolean done = false;
        while(iterator.hasNext() && !done){
            currentNode = iterator.next();
            if(iterator.getDepth(currentNode) > depth){
                done = true;
            }else{
                subGraph.addVertex(currentNode);
            }
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
