package ch.proximity.proximityserver;

import ch.proximity.proximityserver.database.DBWriter;
import ch.proximity.proximityserver.database.FakeDB;
import ch.proximity.proximityserver.suipoller.EventPoller;
import ch.proximity.proximityserver.suipoller.EventPollerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProximityServerApplication {

    public static void main(String[] args) {
        EventPoller eventPoller = new EventPollerImpl();
        DBWriter db = FakeDB.getInstance();
        eventPoller.registerOnNewNodes((ns) -> db.addNodes(ns));
        eventPoller.registerOnNewEdges((es) -> db.addEdges(es));
        eventPoller.start();
        SpringApplication.run(ProximityServerApplication.class, args);
        eventPoller.stop();
    }

}
