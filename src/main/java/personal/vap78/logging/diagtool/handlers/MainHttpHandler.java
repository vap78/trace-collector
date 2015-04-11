package personal.vap78.logging.diagtool.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.TraceConfiguration;

public class MainHttpHandler extends AbstractHttpHandler {

  private static final String TEMPLATE = readTemplate("main.html");
  
  @Override
  public void service(Request req, Response resp) throws Exception {
    super.service(req, resp);
    
    Session session = getSession(req, resp);
    if (session == null) {
      resp.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }

    String host = session.getHost();
    String account = session.getAccount();
    String application = session.getApplication();
    String user = session.getUser();
    
    String content = TEMPLATE.replace("${host}", host).replace("${account}", account)
         .replace("${application}", application).replace("${user}", user);
    TraceConfiguration.addFromDirectory(new File("."));
    ArrayList<String> traceConfigurations = new ArrayList<>(TraceConfiguration.getAllConfigurationNames());
    if (traceConfigurations.size() > 0) {
      Collections.sort(traceConfigurations);
      StringBuilder optionsBuilder = new StringBuilder();
      StringBuilder jsArrayBuilder = new StringBuilder();
      //traceConfigs['security'] = ['test1', 'test2'];

      for (String cfgName : traceConfigurations) {
        optionsBuilder.append("<option value=\"").append(cfgName).append("\">");
        optionsBuilder.append(cfgName).append("</option>").append("\n");
        jsArrayBuilder.append("traceConfigs['").append(cfgName).append("'] = ['");
        TraceConfiguration cfg = TraceConfiguration.getByName(cfgName);
        List<String> locations = cfg.getLocations();
        for (int i = 0; i < locations.size(); i++) {
          jsArrayBuilder.append(locations.get(i)).append("'");
          if (i == locations.size() - 1) {
            jsArrayBuilder.append("];\n");
          } else {
            jsArrayBuilder.append(", '");
          }
        }
      }
      content = content.replace("${incidents}", optionsBuilder.toString());
      content = content.replace("//${jsLocations}", jsArrayBuilder.toString());
      content = content.replace("${locations}",  TraceConfiguration.getByName(traceConfigurations.get(0)).getLocationsAsString());
    } else {
      content = content.replace("${incidents}", "");
    }
    
    if (session.getCurrentTracesCollectionInfo() != null) {
      content = content.replace("${statusclass}", "collecting");
      content = content.replace("${statustext}", "Collecting Traces");
    } else {
      content = content.replace("${statusclass}", "idle");
      content = content.replace("${statustext}", "Not Collecting Traces");
    }
    
    resp.getWriter().write(content);
    resp.getWriter().flush();
  }
}
