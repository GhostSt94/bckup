package starter.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import starter.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {
    static final Logger log = LogManager.getLogger(MainVerticle.class);
    private LocalSessionStore session_store=LocalSessionStore.create(Vertx.vertx(),"auth.session",10000);
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        try {
            log.debug("start MainVerticle ...");

            Router router = Router.router(vertx);
            router.route().handler(BodyHandler.create());
            router.route().handler(SessionHandler.create(session_store));


            //router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.PUT).allowedMethod(HttpMethod.DELETE));
            router.route().pathRegex("/api[/]?.*").handler(AuthHandler::checkAuthAPI);

            //receive and save facture file in resources & DB (file_name)
            router.post("/api/files/:type/:id").handler(FileHandler::saveFile);
            router.get("/api/files/:type/:fileName").handler(FileHandler::getFile);

            //clients
            router.get("/api/clients").handler(ClientsHandler::clients);
            router.post("/api/clients").handler(ClientsHandler::addClient);
            router.get("/api/clients/:id").handler(ClientsHandler::getClient);
            router.put("/api/clients/:id").handler(ClientsHandler::updateClient);
            router.delete("/clients/:id").handler(ClientsHandler::deleteClient);
            //Project
            router.get("/api/projects").handler(ProjectHandler::projects);
            router.get("/api/projects/:id").handler(ProjectHandler::getProject);
            router.post("/api/projects").handler(ProjectHandler::addProject);
            router.put("/api/projects/:id").handler(ProjectHandler::updateProject);
            router.delete("/api/projects/:id").handler(ProjectHandler::deleteProject);
            router.delete("/api/projects/:id/file").handler(ProjectHandler::removeFile);
            //Facture
            router.get("/api/factures/project/:id").handler(FactureHandler::factures);
            router.get("/api/factures/one/:id").handler(FactureHandler::getFacture);
            router.put("/api/factures/:id").handler(FactureHandler::updateFacture);
            router.post("/api/factures").handler(FactureHandler::addFacture);
            router.delete("/api/factures/:id").handler(FactureHandler::deleteFacture);

            //Auth
            router.post("/auth/register").handler(AuthHandler::register);
            router.post("/auth/login").handler(AuthHandler::login);
            router.post("/auth/check").handler(AuthHandler::checkAuth);
            router.post("/auth/logout").handler(AuthHandler::logOut);

            //static
            StaticHandler vueProjectHandler = StaticHandler.create("web/dist/")
                    .setIndexPage("index.html")
                    .setCachingEnabled(true)
                    .setDefaultContentEncoding("UTF-8");
            router.route().handler(vueProjectHandler);
            router.route("/*").handler(ctx -> ctx.reroute("/"));

            vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
                if (http.succeeded()) {
                    startPromise.complete();
                    log.debug("HTTP server started on port 8888");
                } else {
                    log.error(http.cause().getMessage(), http.cause());
                    startPromise.fail(http.cause());
                }
            });
        } catch (Exception e) {
            log.error(e, e);
            startPromise.fail("MainVerticle deployment failed !!!!");
        }
    }

/*    public void login(RoutingContext ctx){
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
    public void logout(RoutingContext ctx){
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

    public void checkAuth(RoutingContext ctx){
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
    public static JsonObject setMessage(String type,JsonObject credentials){
        return new JsonObject()
                .put("type",type)
                .put("credentials",credentials);
    }*/
}
