var vertx = require('vertx');

var container = require('vertx/container');

var console = require('vertx/console');

var appConfig = container.config;

var eb = require('vertx/event_bus');

var ADDRESS_1 = "pingOne";
var ADDRESS_2 = "pingTwo";

var counter1 = 0;
var counter2 = 0;

	vertx.setPeriodic(3000, function sendmessage()
            {
                eb.send(ADDRESS_1, 'ping1!, counter1 = ' + counter1++, function(reply)
                {
                    console.log("received reply1: " + reply);
                });
            });   

		vertx.setPeriodic(5000, function sendmessage()
            {
                eb.send(ADDRESS_2, 'ping2!, counter2 = ' + counter2++, function(reply)
                {
                    console.log("received reply2: " + reply);
                });
            });   

		console.log("Skickar meddelanden på adress \"" + ADDRESS_1 + "\".");
		console.log("Skickar meddelanden på adress \"" + ADDRESS_2 + "\".");



