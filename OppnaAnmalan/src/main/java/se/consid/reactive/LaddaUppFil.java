package se.consid.reactive;

import java.util.UUID;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.platform.Verticle;

/**
 * Created by Johan on 2014-03-30.
 */
public class LaddaUppFil extends Verticle {

//    @Override
    public void start2() {

        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {
                container.logger().info("start");
                req.response().putHeader("Access-Control-Allow-Origin", "*");

                // We first pause the request so we don't receive any data between now and when the file is opened
                req.pause();

                final String filename = "/temp/vertx/file-" + UUID.randomUUID().toString() + ".tmp";

                vertx.fileSystem().open(filename, new AsyncResultHandler<AsyncFile>() {
                    @Override
                    public void handle(AsyncResult<AsyncFile> ar) {
                        if (ar.failed()) {
                            ar.cause().printStackTrace();
                            return;
                        }
                        final AsyncFile file = ar.result();
                        final Pump pump = Pump.createPump(req, file);
                        final long start = System.currentTimeMillis();
                        req.endHandler(new VoidHandler() {
                            @Override
                            public void handle() {
                                file.close(new AsyncResultHandler<Void>() {
                                    @Override
                                    public void handle(AsyncResult<Void> ar) {
                                        if (ar.succeeded()) {
                                            req.response().end();
                                            long end = System.currentTimeMillis();
                                            container.logger().info("LaddaUppFil " + pump.bytesPumped() + " bytes to " + filename + " in " + (end - start) + " ms");
                                        } else {
                                            ar.cause().printStackTrace(System.err);
                                        }
                                    }
                                });
                            }
                        });
                        pump.start();
                        req.resume();
                    }
                });
            }
        }).listen(8081);
    }

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {
                final String filename = "/temp/vertx/file-" + UUID.randomUUID().toString();
                if (req.uri().equals("/")) {
                    container.logger().info("rooten laddas");
                    // Serve the index page
                    req.response().putHeader("Access-Control-Allow-Origin", "*");
                    req.response().sendFile("index.html");
                } else if (req.uri().startsWith("/form")) {
                    container.logger().info("form laddas");
                    req.response().putHeader("Access-Control-Allow-Origin", "*");
                    req.expectMultiPart(true);
                    container.logger().info("form is multipart");
                    req.uploadHandler(new Handler<HttpServerFileUpload>() {
                        @Override
                        public void handle(final HttpServerFileUpload upload) {
                            container.logger().info("handle upload");
                            upload.exceptionHandler(new Handler<Throwable>() {
                                @Override
                                public void handle(Throwable event) {
                                    container.logger().info("Upload failed");
                                    req.response().end("Upload failed");
                                }
                            });
                            upload.endHandler(new Handler<Void>() {
                                @Override
                                public void handle(Void event) {
                                    container.logger().info("Upload successful, you should see the file in the server directory");
                                    req.response().end("Upload successful, you should see the file in the server directory");
                                }
                            });
                            container.logger().info("saving file");
                            upload.streamToFileSystem(filename + upload.filename().substring(upload.filename().lastIndexOf('.')));
                        }
                    });
                    container.logger().info("form har laddats klart");
                } else {
                    container.logger().info("404 laddas");
                    req.response().setStatusCode(404);
                    req.response().end();
                }
            }
        }).listen(8081);
    }

}
