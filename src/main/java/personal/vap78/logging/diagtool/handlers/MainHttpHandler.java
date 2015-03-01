package personal.vap78.logging.diagtool.handlers;

import java.util.Collection;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.TraceConfiguration;

public class MainHttpHandler extends AbstractHttpHandler {

  private static final String TEMPLATE = readTemplate("main.html");
  
  @Override
  public void service(Request req, Response resp) throws Exception {
    Session session = getSession(req, resp);
    if (session == null) {
      resp.sendRedirect("/login.html");
      return;
    }
    super.service(req, resp);

    String host = session.getHost();
    String account = session.getAccount();
    String application = session.getApplication();
    String user = session.getUser();
    
    String content = TEMPLATE.replace("${host}", host).replace("${account}", account)
         .replace("${application}", application).replace("${user}", user);
    
    Collection<String> traceConfigurations = TraceConfiguration.getAllConfigurationNames(); 
    StringBuilder builder = new StringBuilder();
    for (String cfgName : traceConfigurations) {
      builder.append("<option value=\">");
      builder.append(cfgName);
      builder.append("\">");
      builder.append(cfgName);
      builder.append("</option>");
      builder.append("\n");
    }
    content = content.replace("${incidents}", builder.toString());
    
    resp.getWriter().write(content);
  }
}
