package starter.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientsHandler {
  static final Logger log=LogManager.getLogger(ClientsHandler.class);
  final static String TYPE="client";

  public static void clients(RoutingContext ctx){
    try {
      CRUD.findAll(ctx, getJsonType());
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void getClient(RoutingContext ctx){
    try {
      CRUD.findOne(ctx, getJsonType().put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void updateClient(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.updateOne(ctx, data.put("id", ctx.pathParam("id")).put("type", TYPE));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void addClient(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.addOne(ctx, data.put("type", TYPE));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void deleteClient(RoutingContext ctx) {
    try {
      CRUD.deleteOne(ctx, getJsonType().put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }
}
