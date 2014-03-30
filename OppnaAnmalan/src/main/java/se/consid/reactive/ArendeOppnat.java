package se.consid.reactive;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by roland on 2014-03-30.
 */
public class ArendeOppnat extends Verticle{


    @Override
    public void start() {
        EventBus eb = vertx.eventBus();

        Handler<Message> myHandler = new Handler<Message>() {
            public void handle(Message message) {
                System.out.println("I received a message " + message.body());
            }
        };

        eb.registerHandler("arende.oppnat", myHandler);

    }

}