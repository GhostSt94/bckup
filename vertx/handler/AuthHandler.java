package starter.handler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthHandler {
  static final Logger log = LogManager.getLogger(AuthHandler.class);

  public static void register(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("auth.user", setMessage("register",ctx.getBodyAsJson()), rep -> {
        if (rep.succeeded()) {
          ctx.response().setStatusCode(200).send(rep.result().body().toString());
        } else {
          ctx.response().setStatusCode(403).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void login(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("auth.user", setMessage("login",ctx.getBodyAsJson()), rep -> {
        if (rep.succeeded()) {
          JsonObject obj= (JsonObject) rep.result().body();
          String session_id=ctx.session().id();
          ctx.session().put(session_id,obj);
          ctx.response().setStatusCode(200).send(session_id);
        } else {
          ctx.response().setStatusCode(403).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void checkAuthAPI(RoutingContext ctx) {
    try {
      String session_id=ctx.request().headers().get("authorization");
      if(session_id==null){
        log.warn("authorization null");
        ctx.redirect("/auth");
      }else {
        if (ctx.session().get(session_id)==null) {
          log.warn("session id not found");
          ctx.redirect("/auth");
        } else {
          JsonObject user = ctx.session().get(session_id);
          log.debug("({}) - api handler {}",user.getString("username") ,ctx.request().path());
          ctx.next();
        }
      }
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void checkAuth(RoutingContext ctx){
    try {
      String session_id_front=ctx.getBodyAsJson().getString("session_id");
      if (session_id_front == null) {
        log.warn("not session id");
        ctx.response().setStatusCode(400).end("not connected");
      } else if (ctx.session().get(session_id_front) == null) {
        log.warn("not connected (user not found in session)");
        ctx.response().setStatusCode(400).end("not connected");
      } else {
        JsonObject user= ctx.session().get(session_id_front);
        log.info("connected (check auth)");
        ctx.response().setStatusCode(200).end(user.toBuffer());
      }
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static void logOut(RoutingContext ctx) {
    try {
      String session_id_front=ctx.getBodyAsJson().getString("session_id");
      if(session_id_front!=null) {
        if (ctx.session().get(session_id_front) == null) {
          ctx.response().setStatusCode(200).send("already logged out");
        } else {
          ctx.session().remove(session_id_front);
          ctx.response().setStatusCode(200).send("logged out successfully");
        }
      }else{
        ctx.response().setStatusCode(200).send("already logged out");
      }
    }catch (Exception e){
      log.error(e,e);
    }
  }

  public static JsonObject setMessage(String type,JsonObject credentials){
    return new JsonObject()
            .put("type",type)
            .put("credentials",credentials);
  }
}

