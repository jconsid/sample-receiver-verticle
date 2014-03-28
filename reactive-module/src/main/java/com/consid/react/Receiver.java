package com.consid.react;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class Receiver extends Verticle {
    public void start() {
        vertx.eventBus().registerHandler("ping-address", new Handler<Message<JsonObject>>() {
            // @Override
            public void handle(Message<JsonObject> message) {
            // Reply to it
            System.out.println("Received message: " + message.body());
            container.logger().info("Websockets message received: " + message.body());

            final JsonObject replyMessage = new JsonObject();
            replyMessage.putString("results", "ok");
            replyMessage.putString("action", message.body().getString("action"));
            message.reply(replyMessage);
            }
        });
    }
}
