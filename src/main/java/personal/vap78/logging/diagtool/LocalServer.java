package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.security.SecureRandom;

import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;

public class LocalServer {
  
  private static final int THIRTY_MINUTES = 30*60*1000;
  private static final String UTF_8 = "UTF-8";
  private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
  private HttpServer server;

  public LocalServer() {
    server = HttpServer.createSimpleServer("resources", 4242);
    server.getServerConfiguration().addHttpHandler(new HttpHandler() {
      
      @Override
      public void service(Request req, Response resp) throws Exception {
        if (req.getMethod().equals(Method.POST)) {
//          String host = req.getParameter(AbstractLogCommand.HOST_PARAM);
//          String account = req.getParameter(AbstractLogCommand.acc)
//          String user = req.getParameter(AbstractLogCommand.USER_PARAM);
//          String password = req.getParameter(AbstractLogCommand.PASSWORD_PARAM);
          Properties props = new Properties();
          Map<String, String> params = convertParameterMap(req.getParameterMap());
          props.putAll(params);
          
          ListLogFilesCommand command = new ListLogFilesCommand(props);
          try {
            BufferedReader reader = command.executeConsoleTool();
            Map<String, LogFileDescriptor> logFiles = command.parseListLogsOutput(reader);
            
            UUID uuid = createUUID();
            Session session = Session.createSession(uuid.toString(), props);
            session.setLogs(logFiles);
            
            Cookie sessionCookie = new Cookie("sessionId", uuid.toString());
            sessionCookie.setMaxAge(THIRTY_MINUTES);
            resp.addCookie(sessionCookie);
            resp.sendRedirect("main.html");
          } catch (CommandExecutionException e) {
            if (e.getCommand().getConsoleOutput() != null) {
              printFailedLogin(resp);
              return;
            }
          }
        } else {
          resp.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
        }
      }


      private void printFailedLogin(Response resp) {
        // TODO Auto-generated method stub
        
      }

    }, "/doLogin");
  }
  
  private UUID createUUID() {
    SecureRandom random = new SecureRandom();
    return new UUID(random.nextLong(), random.nextLong());
  }

  private Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
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
