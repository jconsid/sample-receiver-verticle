package se.consid.reactive;

import org.vertx.java.platform.Verticle;

/**
 *
 * @author JOHA
 */
public class ListenerOne extends Verticle {

    private static final String ADDRESS = "pingOne";

    @Override
    public void start() {
        vertx.eventBus().registerHandler(ADDRESS, message -> {
            container.logger().info(message.body());
            message.reply("ListenerOne");
        });

        container.logger().info("Lyssnar på adress \"" + ADDRESS + "\".");
    }

}
