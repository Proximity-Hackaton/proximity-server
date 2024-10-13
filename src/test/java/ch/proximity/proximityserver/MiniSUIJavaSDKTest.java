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
                    "0x1f287b94919e183b17caff67e629b05257fc1517b6176c375c70d9f4cdbb6462",
                    "proximity",
                    "NewUserEvent",
                    null, 100
            ).second();
            for (JsonElement elem : elems){
                System.out.println(elem);
            }
        } catch (MiniSUIJavaSDK.SUIRequestException e) {
            throw new RuntimeException(e);
        }

    }
}
