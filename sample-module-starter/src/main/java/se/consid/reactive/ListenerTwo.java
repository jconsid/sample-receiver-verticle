package se.consid.reactive;

import org.vertx.java.platform.Verticle;

/**
 *
 * @author JOHA
 */
public class ListenerTwo extends Verticle {

    private static final String ADDRESS = "pingTwo";

    @Override
    public void start() {
        vertx.eventBus().registerHandler(ADDRESS, message -> {
            container.logger().info(message.body());
            message.reply("ListenerTwo");
        });

        container.logger().info("Lyssnar på adress \"" + ADDRESS + "\".");
    }

}
