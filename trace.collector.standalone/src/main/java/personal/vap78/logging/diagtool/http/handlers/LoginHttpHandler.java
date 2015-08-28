package personal.vap78.logging.diagtool.http.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import personal.vap78.logging.diagtool.api.LoginOperation;
import personal.vap78.logging.diagtool.http.LocalServer;
import personal.vap78.logging.diagtool.impl.console.ConsoleOperationsFactory;
import personal.vap78.logging.diagtool.impl.console.ConsoleSession;
import personal.vap78.logging.diagtool.impl.console.cmd.AbstractLogCommand;

public class LoginHttpHandler extends AbstractHttpHandler {

  public static final String STORED_TIME = "storedTime";

  private static final int THIRTY_MINUTES = 30*60;
  
  private static final String TEMPLATE = readTemplate("login.html");
  
  @Override
  public void service(Request req, Response resp) throws Exception {
    super.service(req, resp);
    if (req.getMethod().equals(Method.POST)) {
      processLogon(req, resp);
    } else if (req.getMethod().equals(Method.GET)) {
      logonScreen(null, null, resp);
    } else {
      resp.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
    }
  }

  private void processLogon(Request req, Response resp) throws IOException, Exception {
    Properties props = new Properties();
    Map<String, String> params = LocalServer.convertParameterMap(req.getParameterMap());
    props.putAll(params);

    LoginOperation loginOperation = ConsoleOperationsFactory.getLoginOperation(props);
    
    loginOperation.execute(null);
    
    if (loginOperation.isSuccessful()) {
      ConsoleSession session = (ConsoleSession) loginOperation.getSession();
      Cookie sessionCookie = new Cookie(SESSION_ID, session.getId());
      sessionCookie.setMaxAge(THIRTY_MINUTES);
      resp.addCookie(sessionCookie);
      resp.sendRedirect("/main");
    } else {
      printFailedLogin(props, resp);
    }
  }

  private void logonScreen(String errorMessage, Properties previousSessionProps, Response resp) throws IOException {
    String content = TEMPLATE;
    if (errorMessage == null) {
      content = content.replace("${errorMessage}", "<div id=\"errormessage\" class=\"hidden\"></div>");
    } else {
      content = content.replace("${errorMessage}", "<div id=\"errormessage\" class=\"shown\">" + errorMessage + "</div>");
    }
    List<ConsoleSession> sessions = readPreviousSessions();
    ConsoleSession lastSession = null;
    StringBuilder sessionsSelectBuilder = new StringBuilder();
    StringBuilder jsArrayBuilder = new StringBuilder();
    
    if (previousSessionProps == null) {
      for (ConsoleSession s : sessions) {
        if (lastSession == null) {
          lastSession = s;
        } else {
          String storeTimeStr = s.getProperties().getProperty(STORED_TIME, "-1");
          String lastSessionStoreTimeStr = lastSession.getProperties().getProperty(STORED_TIME, "-1");
          if (Long.parseLong(lastSessionStoreTimeStr) < Long.parseLong(storeTimeStr)) {
            lastSession = s;
          }
        }
      }
    } else {
      lastSession = new ConsoleSession(null, previousSessionProps);
    }
    
    for (ConsoleSession s : sessions) {
      sessionsSelectBuilder.append("<option value=\"");
      sessionsSelectBuilder.append(s.getLongName());
      sessionsSelectBuilder.append("\"");
      if (s == lastSession) {
        sessionsSelectBuilder.append(" selected>");
      } else {
        sessionsSelectBuilder.append(">");
      }
      sessionsSelectBuilder.append("Host: ");
      sessionsSelectBuilder.append(s.getHost());
      sessionsSelectBuilder.append(" Account: ");
      sessionsSelectBuilder.append(s.getAccount());
      sessionsSelectBuilder.append(" Application: ");
      sessionsSelectBuilder.append(s.getApplication());
      sessionsSelectBuilder.append("</option>\n");
      
    //sessions['xyz'] = {sdkPath: '/test', host: 'host', account: 'account1', application: 'app', user: 'user', proxy: 'proxy'};
      jsArrayBuilder.append("sessions['").append(s.getLongName()).append("'] = {sdkPath: '");
      jsArrayBuilder.append(s.getSDKPath()).append("', host: '").append(s.getHost());
      jsArrayBuilder.append("', account: '").append(s.getAccount()).append("', application: '");
      jsArrayBuilder.append(s.getApplication()).append("', user: '").append(s.getUser());
      jsArrayBuilder.append("', proxy: '").append(s.getProxy()).append("', proxyUser: '").append(s.getProxyUser());
      jsArrayBuilder.append("'};");
    }
    content = content.replace("//${sessions.js}", jsArrayBuilder.toString());
    content = content.replace("${sessions}", sessionsSelectBuilder.toString());
    if (lastSession == null) {
      content = content.replace("${host}", "").replace("${account}", "")
          .replace("${application}", "").replace("${user}", "").replace("${proxy}", "")
          .replace("${proxyUser}", "").replace("${sdkPath}", "");
    } else {
      content = content.replace("${host}", lastSession.getHost())
          .replace("${account}", lastSession.getAccount())
          .replace("${application}", lastSession.getApplication())
          .replace("${user}", lastSession.getUser())
          .replace("${sdkPath}", lastSession.getSDKPath())
          .replace("${proxy}", lastSession.getProxy())
          .replace("${proxyUser}", lastSession.getProxyUser());
    }
    
    resp.getWriter().write(content);
    resp.getWriter().flush();
  }

  private List<ConsoleSession> readPreviousSessions() throws IOException {
    List<ConsoleSession> toReturn = new ArrayList<>();
    File root = new File(".");
    String[] localFiles = root.list();
    
    for(String fileName : localFiles) {
      File file = new File(root, fileName);
      
      if (!file.isDirectory() && fileName.endsWith(".session") && fileName.split("_").length == 3) {
        FileInputStream input = null;
        try {
          Properties props = new Properties();
          input = new FileInputStream(file);
          props.load(new InputStreamReader(input, UTF_8));
          if (props.getProperty(AbstractLogCommand.HOST_PARAM) != null &&
              props.getProperty(AbstractLogCommand.ACCOUNT_PARAM) != null &&
              props.getProperty(AbstractLogCommand.APPLICATION_PARAM) != null) {
            ConsoleSession session = new ConsoleSession(null, props);
            toReturn.add(session);
          }
        } catch (Exception e) {
          System.out.println("Warning: unable to parse file: " + fileName + ". Ignoring it.");
          e.printStackTrace();
        } finally {
          if (input != null) {
            input.close();
          }
        }
      }
    }
    
    return toReturn;
  }

  private void storeProperties(ConsoleSession session) throws IOException {
    Properties props = (Properties) session.getProperties().clone();
    props.remove(AbstractLogCommand.PASSWORD_PARAM);
    props.setProperty(STORED_TIME, String.valueOf(System.currentTimeMillis()));
    
    String host = session.getHost();
    String account = session.getAccount();
    String application = session.getApplication();

    String fileName = host + "_" + account + "_" + application + ".session";
    
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(fileName);
      props.store(new OutputStreamWriter(fos, UTF_8), null);
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }

  private void printFailedLogin(Properties lastSession, Response resp) throws IOException {
    logonScreen("Logon failed", lastSession, resp);
  }
}