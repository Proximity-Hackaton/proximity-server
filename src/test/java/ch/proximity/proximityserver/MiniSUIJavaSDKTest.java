package ch.proximity.proximityserver;

import ch.proximity.proximityserver.suipoller.MiniSUIJavaSDK;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MiniSUIJavaSDKTest {
    @Test
    void testQueryEvents(){
        MiniSUIJavaSDK sui = new MiniSUIJavaSDK();
        try {
            List<JsonElement> elems = sui.queryEvents(
                    "0xfd9a3688cf778f10b455d89cf5b60d00ac698722843f69460468c8b64eecbc31",
                    "proximity",
                    "NodeUpdateEvent",
                    null, 100
            ).second();
            System.out.println(elems.get(0));
        } catch (MiniSUIJavaSDK.SUIRequestException e) {
            throw new RuntimeException(e);
        }

    }
}
