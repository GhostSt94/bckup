package starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import starter.verticles.DbService;
import starter.verticles.MainVerticle;

public class Main extends AbstractVerticle {
  static final Logger log = LogManager.getLogger(Main.class);

  public static void main(String[] args){
    try {
      Vertx vertx = Vertx.vertx();
      vertx.deployVerticle(new MainVerticle(), res -> {
        if (res.succeeded()) {
          log.debug("MainVerticle has deployed");
          vertx.deployVerticle(new DbService(),h->{
            if (h.succeeded()) {
              log.debug("DbService has deployed");
            }else{
              log.error(h.cause().getMessage(),h.cause());
            }
          });
        }else{
          log.error(res.cause().getMessage(),res.cause());
        }
      });
    }catch (Exception e){
      log.error(e,e);
    }
  }
}
