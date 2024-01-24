package net.company.gongonchu;

import com.launchdarkly.eventsource.*;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class EventStreamReader
{
    private static final Logger LOG = LoggerFactory.getLogger(EventStreamReader.class);

    private final String ATTRIBUTE_01 = "type";
    private final String ATTRIBUTE_02 = "title";
    private final String TYPE_ENDING_STREAM = "\"categorize\"";
    private final String URL = "https://stream.wikimedia.org/v2/stream/recentchange";

    private final Boolean STREAM_CONTINUOUSLY = Boolean.FALSE;

    public static void main(String[] args){
        EventStreamReader javaSampler = new EventStreamReader();
        javaSampler.callStream();
    }

    public void callStream(){
        Iterable<MessageEvent> messagesList = new ArrayList<MessageEvent>();
        MessageEvent msgEvent = null;
        JsonReader jsonReader = null;
        JsonObject jsonObject = null;
        JsonValue type = null;
        JsonValue title = null;
        String msg = null;

        try {
            //EventSource.Builder sourceBuilder = new EventSource.Builder(new URI("https://stream.wikimedia.org/v2/stream/recentchange")).retryDelay(3l, TimeUnit.SECONDS);
            EventSource.Builder sourceBuilder = new EventSource.Builder(
                    ConnectStrategy
                            .http(new URI(URL))
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
            );

            EventSource source = sourceBuilder.build();
            source.start();

            messagesList = source.messages();

            for(MessageEvent message : messagesList){
                msg = message.getData();
                LOG.info(msg);
                jsonReader = Json.createReader(new StringReader(msg));
                jsonObject = jsonReader.readObject();
                type = jsonObject.getJsonString(ATTRIBUTE_01);
                title = jsonObject.getJsonString(ATTRIBUTE_02);
                LOG.info(ATTRIBUTE_01 + " : " + type + " / " + ATTRIBUTE_02 + ": " + title);
                if(!STREAM_CONTINUOUSLY && TYPE_ENDING_STREAM.equalsIgnoreCase(type.toString())){
                    source.close();
                    break;
                }
            }

            LOG.info("END");

        }catch (URISyntaxException e){
            ExceptionDisplayUtility.displayExceptionMessage("URI error", e);
        }
        catch (StreamException e){
            ExceptionDisplayUtility.displayExceptionMessage("Stream error", e);
        }

    }




}
