package personal.vap78.logging.diagtool.http;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.glassfish.grizzly.http.server.HttpServer;

import personal.vap78.logging.diagtool.http.handlers.DownloadLogsHandler;
import personal.vap78.logging.diagtool.http.handlers.LoginHttpHandler;
import personal.vap78.logging.diagtool.http.handlers.MainHttpHandler;
import personal.vap78.logging.diagtool.http.handlers.ReportListHandler;
import personal.vap78.logging.diagtool.http.handlers.StartTracesCollectionHttpHandler;
import personal.vap78.logging.diagtool.http.handlers.StopTracesCollectionHttpHandler;

public class LocalServer {
  
  private HttpServer server;

  public LocalServer() {
    server = HttpServer.createSimpleServer("resources", 4242);
    server.getServerConfiguration().addHttpHandler(new LoginHttpHandler(), "/doLogin");
    server.getServerConfiguration().addHttpHandler(new MainHttpHandler(), "/main");
    server.getServerConfiguration().addHttpHandler(new StartTracesCollectionHttpHandler(),"/startTraces");
    server.getServerConfiguration().addHttpHandler(new StopTracesCollectionHttpHandler(),"/stopTraces");
    server.getServerConfiguration().addHttpHandler(new DownloadLogsHandler(),"/getLog");
    server.getServerConfiguration().addHttpHandler(new ReportListHandler(),"/traces");
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
      System.exit(1);
    }
  }
}
