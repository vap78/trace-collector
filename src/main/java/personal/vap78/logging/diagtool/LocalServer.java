package personal.vap78.logging.diagtool;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.glassfish.grizzly.http.server.HttpServer;

import personal.vap78.logging.diagtool.handlers.LoginHttpHandler;
import personal.vap78.logging.diagtool.handlers.MainHttpHandler;
import personal.vap78.logging.diagtool.handlers.TracesCollectionHttpHandler;

public class LocalServer {
  
  private HttpServer server;

  public LocalServer() {
    server = HttpServer.createSimpleServer("resources", 4242);
    server.getServerConfiguration().addHttpHandler(new LoginHttpHandler(), "/doLogin");
    server.getServerConfiguration().addHttpHandler(new MainHttpHandler(), "/main");
    server.getServerConfiguration().addHttpHandler(new MainHttpHandler(), "/main");
    server.getServerConfiguration().addHttpHandler(new TracesCollectionHttpHandler(),"/doTraces");
  }
  
  public static UUID createUUID() {
    SecureRandom random = new SecureRandom();
    return new UUID(random.nextLong(), random.nextLong());
  }

  public static Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
    Map<String, String> toReturn = new HashMap<String, String>();
    for (String key : parameterMap.keySet()) {
      String[] value = parameterMap.get(key);
      if (value != null && value.length > 0) {
        toReturn.put(key, parameterMap.get(key)[0]);
      }
    }
    return toReturn;
  }
  
  public void start() {
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to start server");
      System.exit(5);
    }
  }
}
