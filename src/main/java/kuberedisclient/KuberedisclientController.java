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
    RedisClusterClient redisClusterClient;
    RedisAdvancedClusterCommands<String, String> sync;

    public KuberedisclientController() {
        try  {
            System.out.println("Connecting to redis cluster ..");
            StatefulRedisClusterConnection<String, String> connection = redisClusterClient.connect();
            RedisAdvancedClusterCommands<String, String> sync = connection.sync();
            System.out.println("Succesfully connected.");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Get("/test")
    String test() {
        try  {
            sync.set("key", "Hello, World!");
            String value = sync.get("key");
            System.out.println(value);
            return "{ 'value': '" + value + "'}";
        } catch (Exception e) {
            System.out.println(e.toString());
            return "{ 'error': '" + e.toString() + "'}";
        }
    }
}

