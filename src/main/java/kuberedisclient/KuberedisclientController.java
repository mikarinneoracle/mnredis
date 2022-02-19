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

    @Get("/test")
    String test() {
        if(connection == null) {
            System.out.println("Connecting to redis cluster ..");
            int i=0;
            while ( i < 5 && connection == null)
            {
                if(i > 0)
                {
                    System.out.println("Retrying ...");
                }
                try {
                    connection = redisClient.connect();
                } catch (Exception e) {
                    System.out.println("Failed. Error:" + e.toString());
                    connection = null;
                }
                i++;
            }
            if(connection == null)
            {
                return "{ \"Test error\": \"Could not connect to Redis\" }";
            }
        }
        if(connection != null) {
            try {

                RedisCommands sync = connection.sync();
                sync.set("key", "Hello, World!");
                String value = (String) sync.get("key");
                System.out.println(value);
                return "{ \"value\": \"" + value + "\"}";
            } catch (Exception e) {
                connection = null;
                return "{ \"Test error\": \"" + e.toString() + "\" }";
            }
        }
        return "{ \"Test error\": \"Something went wrong\" }";
    }
}

