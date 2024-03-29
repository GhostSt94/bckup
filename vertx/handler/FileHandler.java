package starter.handler;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class FileHandler {
  static final Logger log = LogManager.getLogger(FileHandler.class);
  public static final String PATH_FACTURE_FILES="src/main/resources/FactureFiles/";
  public static final String PATH_PROJECT_FILES="src/main/resources/ProjectFiles/";

  public static void getFile(RoutingContext ctx){
    try {
      String file_name=ctx.pathParam("fileName");
      String type=ctx.pathParam("type");
      if(setPath(type)==null)
        ctx.response().setStatusCode(404).end("not found");
      else {
        ctx.response().sendFile(setPath(type) + file_name);
      }
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("not found");
      log.error(e,e);
    }
  }

  public static void saveFile(RoutingContext ctx){
    try {
      EventBus eb=ctx.vertx().eventBus();
      FileSystem fs=ctx.vertx().fileSystem();
      String id = ctx.pathParam("id");
      String type = ctx.pathParam("type");
      if (setPath(type)==null){
        ctx.response().setStatusCode(403).end("error handling file");
      }else{
        Set<FileUpload> fileUploadSet = ctx.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
          log.info("no file found");
          ctx.response().setStatusCode(404).end("no file found");
        } else {
          for (FileUpload f : fileUploadSet) {
            fs.readFile(f.uploadedFileName(), buffer -> {
              String file_name = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + f.fileName();
              fs.writeFile(setPath(type) + file_name, buffer.result(), res -> {
                if (res.succeeded()) {
                  fs.delete(f.uploadedFileName());
                  JsonObject obj = new JsonObject()
                          .put("file_name", file_name)
                          .put("id",id)
                          .put("type",type);
                  eb.request("add.file.db", obj, resp -> {
                    if (resp.succeeded()) {
                      JsonObject project= (JsonObject) resp.result().body();
                      if(type.equals("project")&&project.containsKey("file")){
                        deleteFile(fs,type,project.getString("file"));
                      }
                      log.info("added {} file {}",type,file_name);
                      ctx.response().setStatusCode(200).end("File Saved");
                    } else {
                      ctx.response().setStatusCode(400).end("Error saving file");
                    }
                  });
                } else {
                  ctx.response().setStatusCode(400).end("Error writing file");
                }
              });
            });
          }
        }
      }
    }catch (Exception e){
      ctx.response().setStatusCode(400).end("Unexpected Error");
      log.error(e,e);
    }
  }

  public static String setPath(String type){
    String path = null;
    if(type.equals("project")){
      path= PATH_PROJECT_FILES;
    }else if(type.equals("facture")){
      path= PATH_FACTURE_FILES;
    }
    return path;
  }

  public static void deleteFile(FileSystem fs, String type,String fileName){
    try {
      fs.delete(setPath(type) + fileName, voidAsyncResult -> {
        if (voidAsyncResult.succeeded()) {
          log.info("removed {} file {}",type, fileName);
        } else {
          log.info( "{} file {} doesn't exist",type,fileName);
        }
      });
    }catch(Exception e){
      log.error(e,e);
    }
  }

}
