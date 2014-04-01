package se.consid.reactive;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by Anders on 2014-03-30.
 */
public class SkapaLog extends Verticle {

    @Override
    public void start() {
        final Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
            @Override
            public void handle(final Message<JsonObject> request) {
                container.logger().info(request.body());
                final JsonObject body = request.body();

                final JsonObject update = createUpdate(body);

                vertx.eventBus().send("test.mongodb", update, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(final Message<JsonObject> dbResponse) {
                        final JsonObject answer = dbResponse.body();
                        container.logger().info(answer);

                        request.reply(answer);

                        fireEventLogSkapad(body);
                    }
                });
            }
        };

        vertx.eventBus().registerHandler("arende.skapalog", handler, new Handler<AsyncResult<Void>>(){
            @Override
            public void handle(AsyncResult<Void> result) {
                container.logger().info("SkapaLog depoly " + result.succeeded());
            }
        });
    }

    private void fireEventLogSkapad(JsonObject query) {
        vertx.eventBus().publish("arende.logskapad", query);
    }

    protected JsonObject createUpdate(final JsonObject request) {
        final int id = request.getInteger("id");

        final JsonObject upd = new JsonObject();
        upd.putObject("$push", new JsonObject().putObject("loggar", request));

        final JsonObject update = new JsonObject();
        update.putString("action", "update");
        update.putString("collection", "anmalningar");
        update.putObject("criteria", new JsonObject().putNumber("_id", id));
        update.putObject("objNew", upd);

        return  update;
    }

}
