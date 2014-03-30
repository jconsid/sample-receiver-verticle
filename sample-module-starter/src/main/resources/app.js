var vertx = require('vertx');

var container = require('vertx/container');

//var console = require('vertx/console');

var appConfig = container.config;

container.deployVerticle('se.consid.reactive.ListenerOne', appConfig.verticle1Config);
container.deployVerticle('se.consid.reactive.ListenerTwo', appConfig.verticle2Config);
container.deployVerticle('se.consid.reactive.ListenerThree', appConfig.verticle3Config);
container.deployVerticle('hello.js', appConfig.hello);



