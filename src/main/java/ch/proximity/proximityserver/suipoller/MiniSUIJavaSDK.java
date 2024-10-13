package ch.proximity.proximityserver.suipoller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MiniSUIJavaSDK {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String URL = "https://fullnode.testnet.sui.io:443";


    private JsonObject sendRequest(JsonObject jsonElem) throws SUIRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonElem.toString()))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            throw new SUIRequestException();

        } catch (InterruptedException e) {
            throw new SUIRequestException();
        }
        if(response.statusCode() != 200){return null;}

        Gson gson = new Gson();
        return gson.fromJson(response.body(), JsonObject.class);
    }

    private JsonObject createRequestHeader(){
        JsonObject jObj = new JsonObject();
        jObj.addProperty("jsonrpc", "2.0");
        jObj.addProperty("id", 1);
        return jObj;
    }

    private Triple<PagingCursor, List<JsonElement>, Boolean> queryEventsNonRecursive(
            String packageName,
            String moduleName,
            String eventType,
            PagingCursor pagingCursor,
            int maxItems
    ) throws SUIRequestException {
        JsonObject jObj = createRequestHeader();
        JsonArray params = new JsonArray();
        JsonObject filter = new JsonObject();
        //JsonObject moveModule = new JsonObject();
        //moveModule.addProperty("package", packageName);
        //moveModule.addProperty("module", moduleName);
        filter.addProperty("MoveEventType", packageName+"::"+moduleName+"::"+eventType);

        params.add(filter);
        if(pagingCursor != null) {
            JsonObject cursor = new JsonObject();
            cursor.addProperty("txDigest", pagingCursor.getTxDigest());
            cursor.addProperty("eventSeq", pagingCursor.getEventSeq());
            params.add(cursor);
            params.add(maxItems);
            params.add(false);
        }
        jObj.add("params", params);
        jObj.addProperty("method", "suix_queryEvents");

        JsonObject response = sendRequest(jObj);
        if(!response.has("result")){
            throw new SUIRequestException();
        }
        JsonObject result = response.getAsJsonObject("result");
        JsonArray data = result.getAsJsonArray("data");
        boolean hasNextPage = result.get("hasNextPage").getAsBoolean();
        PagingCursor newPagingCursor = null;
        if(result.has("nextCursor")){
            JsonObject nextCursor = result.getAsJsonObject("nextCursor");
            String txDigest = nextCursor.get("txDigest").getAsString();
            String eventSeq = nextCursor.get("eventSeq").getAsString();
            newPagingCursor = new PagingCursor(txDigest, eventSeq);
        }
        return new Triple<>(newPagingCursor, data.asList(), hasNextPage);

    }

    public Pair<PagingCursor, List<JsonElement>> queryEvents(
            String packageName,
            String moduleName,
            String eventType,
            PagingCursor pagingCursor,
            int maxItems
    ) throws SUIRequestException {
        Triple<PagingCursor, List<JsonElement>, Boolean> firstRequest = queryEventsNonRecursive(
                packageName,
                moduleName,
                eventType,
                pagingCursor,
                maxItems
        );
        boolean hasNext = firstRequest.c();
        List<JsonElement> elems = new ArrayList<>(firstRequest.b);
        PagingCursor lastPage = firstRequest.a();
        while(hasNext){
            Triple<PagingCursor, List<JsonElement>, Boolean> req = queryEventsNonRecursive(
                    packageName,
                    moduleName,
                    eventType,
                    lastPage,
                    maxItems
            );
            lastPage = req.a();
            elems.addAll(req.b);
            hasNext = req.c();
        }
        return new Pair<>(lastPage, elems);

    }




    public class SUIRequestException extends Exception{

    }



    public static class PagingCursor {
        private final String txDigest;
        private final String eventSeq;

        public PagingCursor(String txDigest, String eventSeq) {
            this.txDigest = txDigest;
            this.eventSeq = eventSeq;
        }

        public String getTxDigest() {
            return txDigest;
        }

        public String getEventSeq() {
            return eventSeq;
        }
    }
    public record Pair<T,V>(T first, V second) {}

    public record Triple<A,B,C>(A a, B b, C c) {}


}
