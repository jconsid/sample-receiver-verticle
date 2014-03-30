package se.consid.reactive;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.io.IOException;

/**
 * Created by Anders on 2014-03-30.
 */
public class SparaFil extends Verticle {

    @Override
    public void start() {
        final Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                container.logger().info(message.body());
                sparaFil(message.body().getString("text"));
            }
        };

        vertx.eventBus().registerHandler("arende.fil", handler, new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
                container.logger().info("SparaFil deploy " + result.succeeded());
            }
        });
    }

    protected void sparaFil(final String filnamn) {
        try {
            Runtime.getRuntime().exec("mongofiles -d filer put " + filnamn);
        } catch (final IOException e) {
            container.logger().error(e.getMessage());
        }
    }

}
