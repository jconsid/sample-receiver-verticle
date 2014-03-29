

package se.consid.reactive;

        import org.vertx.java.core.Handler;
        import org.vertx.java.core.eventbus.Message;
        import org.vertx.java.core.json.JsonObject;
        import org.vertx.java.platform.Verticle;

public class SearchLogger extends Verticle {
    public void start() {
        System.out.println("Registrerar SearchLogger");
        vertx.eventBus().registerHandler("Consid.SearchLog", new Handler<Message<JsonObject>>() {
            // @Override
            public void handle(Message<JsonObject> message) {
                // Reply to it
                System.out.println("Received message: " + message.body());
                container.logger().info("Websockets message received: " + message.body());
            }
        });
        container.logger().info("SearchLogger started");
    }
}

