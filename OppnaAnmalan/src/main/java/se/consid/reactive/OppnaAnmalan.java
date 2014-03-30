package se.consid.reactive;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;


public class OppnaAnmalan extends Verticle {
	
	//private final Map<String, Set<String>> oppnaArenden = new HashMap<>();

	@Override
	public void start() {

		final Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
			@Override
			public void handle(final Message<JsonObject> request) {
				container.logger().info(request.body());

				final JsonObject query = createQuery(request.body());

				vertx.eventBus().send("test.mongodb", query, new Handler<Message<JsonObject>>() {
					@Override
					public void handle(final Message<JsonObject> dbResponse) {
						final JsonObject answer = dbResponse.body();
						container.logger().info(answer);

                        request.reply(answer.getObject("result"));
					}
				});		
			}
		};
		
		vertx.eventBus().registerHandler("arende.oppna", handler);
	}

    protected JsonObject createQuery(final JsonObject message) {
        final JsonObject query = new JsonObject();
        query.putString("action", "findone");
        query.putString("collection", "anmalningar");
        query.putObject("matcher", new JsonObject().putNumber("_id", Integer.parseInt(message.getString("id"))));

        return query;
    }

}
