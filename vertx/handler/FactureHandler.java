package starter.handler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FactureHandler {
  static final Logger log = LogManager.getLogger(FactureHandler.class);
  final static String TYPE="facture";

  public static void factures(RoutingContext ctx){
    try {
      String id = ctx.pathParam("id");
      CRUD.findAll(ctx, getJsonType().put("id_project", id));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void getFacture(RoutingContext ctx){
    try {
      String id = ctx.pathParam("id");
      CRUD.findOne(ctx, getJsonType().put("id", id));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void addFacture(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.addOne(ctx, data.put("type", TYPE));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void deleteFacture(RoutingContext ctx) {
    try {
      CRUD.deleteOne(ctx, getJsonType().put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void updateFacture(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.updateOne(ctx, data.put("type", TYPE).put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }

}
