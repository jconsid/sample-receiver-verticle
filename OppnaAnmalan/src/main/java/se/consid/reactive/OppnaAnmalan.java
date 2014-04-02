package se.consid.reactive;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


public class OppnaAnmalan extends Verticle {

	@Override
	public void start() {
        container.logger().info("Oppna anmälan startar...");

        container.deployVerticle("se.consid.reactive.ArendeOppnat");
        container.deployVerticle("se.consid.reactive.SkapaLog");

        EventBus eb = vertx.eventBus();

		final Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
			@Override
			public void handle(final Message<JsonObject> request) {
				container.logger().info(request.body());

                final Integer anmalanId = Integer.parseInt(request.body().getString("id"));
                final String username = (String) request.body().removeField("username");
				final JsonObject query = createQuery(request.body());

				vertx.eventBus().send("test.mongodb", query, new Handler<Message<JsonObject>>() {
					@Override
					public void handle(final Message<JsonObject> dbResponse) {

                        final ConcurrentMap<Integer, String> anmalanMap = vertx.sharedData().getMap("anmalan.oppnade");
                        container.logger().info(dbResponse.body());

                        container.logger().info("Logging usage of: " + anmalanId);
                        final String anmalanUsageJson = anmalanMap.get(anmalanId);

                        final JsonArray array = append(anmalanUsageJson, username);

                        vertx.sharedData().getMap("anmalan.oppnade").put(anmalanId, array.encode());


                        final JsonObject answer = dbResponse.body();
                        container.logger().info(answer);

                        request.reply(answer.getObject("result"));

                        fireEventArendeOppnat(query, array);
					}


                });
			}
		};

        eb.registerHandler("arende.oppna", handler);
        container.logger().info("Oppna anmälan startar");


	}

    private JsonArray append(final String anmalanUsageJson, final String username) {
        JsonArray arr = (anmalanUsageJson != null) ? new JsonArray(anmalanUsageJson) : new JsonArray();
        JsonObject obj = new JsonObject();
        obj.putString("username", username);
        obj.putNumber("logTime", new Date().getTime());
        arr.add(obj);
        return arr;
    }

    public static class AnmalanOppnadData {
        private final String username;
        private final Date logTime;

        public AnmalanOppnadData(String username, Date logTime) {
            this.username = username;
            this.logTime = logTime;
        }

        public String getUsername() {
            return username;
        }

        public Date getLogTime() {
            return logTime;
        }
    }

    private void fireEventArendeOppnat(JsonObject query, final JsonArray usageList) {
        JsonObject arendeOppnat = new JsonObject();
        arendeOppnat.putString("user", "user");

        final JsonArray array = new JsonArray();

        arendeOppnat.putArray("usage", usageList);
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
