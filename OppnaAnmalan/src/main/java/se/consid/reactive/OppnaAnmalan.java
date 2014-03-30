package se.consid.reactive;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;


public class OppnaAnmalan extends Verticle {

	@Override
	public void start() {
        container.logger().info("Oppna anmälan startar");
        container.deployVerticle("se.consid.reactive.ArendeOppnat");
        EventBus eb = vertx.eventBus();

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

                        fireEventArendeOppnat(query);
					}
				});		
			}
		};

        eb.registerHandler("arende.oppna", handler);
        container.logger().info("Oppna anmälan startar");


	}

    private void fireEventArendeOppnat(JsonObject query) {
        JsonObject arendeOppnat = new JsonObject();
        arendeOppnat.putString("user", "user");
        arendeOppnat.putElement("query", query);
        vertx.eventBus().publish("arende.oppnat", arendeOppnat);
    }

    protected JsonObject createQuery(final JsonObject message) {
        final JsonObject query = new JsonObject();
        query.putString("action", "findone");
        query.putString("collection", "anmalningar");
        query.putObject("matcher", new JsonObject().putNumber("_id", Integer.parseInt(message.getString("id"))));

        return query;
    }

}
