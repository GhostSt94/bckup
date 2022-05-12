package starter.handler;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectHandler {
  static final Logger log = LogManager.getLogger(ProjectHandler.class);
  final static String TYPE="project";

  public static void projects(RoutingContext ctx){
    try {
      CRUD.findAll(ctx, getJsonType());
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void getProject(RoutingContext ctx){
    try {
      CRUD.findOne(ctx, getJsonType().put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void updateProject(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.updateOne(ctx, data.put("id", ctx.pathParam("id")).put("type", TYPE));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void addProject(RoutingContext ctx) {
    try {
      JsonObject data = ctx.getBodyAsJson();
      CRUD.addOne(ctx, data.put("type", TYPE));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void deleteProject(RoutingContext ctx) {
    try {
      CRUD.deleteOne(ctx, getJsonType().put("id", ctx.pathParam("id")));
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }

  public static void removeFile(RoutingContext ctx) {
    try {
      Vertx vertx = ctx.vertx();
      vertx.eventBus().request("remove.file.db", ctx.pathParam("id"), rep -> {
        if (rep.succeeded()) {
          FileHandler.deleteFile(vertx.fileSystem(), TYPE, rep.result().body().toString());
          ctx.response().setStatusCode(200).end("File deleted");
        } else {
          ctx.response().setStatusCode(404).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e,e);
    }
  }
}
