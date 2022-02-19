package kuberedisclient;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Inject;

@Controller("/redis")
public class KuberedisclientController {

    @Inject
    RedisClient redisClient;
    StatefulRedisConnection<String, String> connection;
    RedisCommands sync;

    @Get("/test")
    String test() {
        if(connection == null || sync == null) {
            System.out.println("Connecting to redis cluster ..");
            int i=0;
            while ( i < 100 && (connection == null || sync == null))
            {
                if(i > 0)
                {
                    System.out.println("Retrying ...");
                }
                try {
                    connection = redisClient.connect();
                    sync = connection.sync();
                } catch (Exception e) {
                    System.out.println("Failed. Error:" + e.toString());
                    connection = null;
                    sync = null;
                }
                i++;
            }
            if(connection == null || sync == null)
            {
                return "{ \"Test error\": \"Could not connect to Redis\" }";
            } else {
                System.out.println("Connected.");
            }
        }
        if(connection != null && sync != null) {
            try {
                sync.set("key", "Hello, World!");
                String value = (String) sync.get("key");
                System.out.println(value);
                return "{ \"value\": \"" + value + "\"}";
            } catch (Exception e) {
                connection = null;
                sync = null;
                return "{ \"Test error\": \"" + e.toString() + "\" }";
            }
        }
        return "{ \"Test error\": \"Something went wrong\" }";
    }
}

