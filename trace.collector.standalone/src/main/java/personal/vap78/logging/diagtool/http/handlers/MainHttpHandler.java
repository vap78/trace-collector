package personal.vap78.logging.diagtool.http.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.impl.console.ConsoleSession;

public class MainHttpHandler extends AbstractHttpHandler {

  private static final String TEMPLATE = readTemplate("main.html");
  
  @Override
  public void service(Request req, Response resp) throws Exception {
    super.service(req, resp);
    
    ConsoleSession session = getSession(req, resp);
    if (session == null) {
      resp.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }

    String host = session.getHost();
    String account = session.getAccount();
    String application = session.getApplication();
    String user = session.getUser();
    
    StringBuilder content = new StringBuilder(TEMPLATE);
    
    replaceAll(content, "${host}", host);
    replaceAll(content, "${account}", account);
    replaceAll(content, "${user}", user);
    replaceAll(content, "${application}", application);

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
        List<String> locations = cfg.getLoggers();
        for (int i = 0; i < locations.size(); i++) {
          jsArrayBuilder.append(locations.get(i)).append("'");
          if (i == locations.size() - 1) {
            jsArrayBuilder.append("];\n");
          } else {
            jsArrayBuilder.append(", '");
          }
        }
      }
      
      replaceAll(content, "${incidents}", optionsBuilder.toString());
      replaceAll(content, "//${jsLocations}", jsArrayBuilder.toString());
      replaceAll(content, "${locations}",  TraceConfiguration.getByName(traceConfigurations.get(0)).getLoggersAsString());
    } else {
      replaceAll(content, "${incidents}", "");
      replaceAll(content, "${locations}", "");
    }
    
    if (session.getCurrentTracesCollectionInfo() != null) {
      replaceAll(content, "${statusclass}", "collecting");
      replaceAll(content, "${statustext}", "Collecting Traces");
    } else {
      replaceAll(content, "${statusclass}", "idle");
      replaceAll(content, "${statustext}", "Not Collecting Traces");
    }
    
    List<String> collectedTraces = session.getCollectedTraceFiles(); 
    if (collectedTraces.size() == 0) {
      replaceAll(content, "${sessionResultsDisplay}", "display: none");
      replaceAll(content, "${sessionResults}", "");
    } else {
      replaceAll(content, "${sessionResultsDisplay}", "display: block");
      StringBuilder tracesHtml = new StringBuilder();
      for (String traceFileName : collectedTraces) {
        tracesHtml.append("<a target=\"_blank\" href=\"/getLog?name=");
        tracesHtml.append(traceFileName);
        tracesHtml.append("\">");
        tracesHtml.append(traceFileName);
        tracesHtml.append("</a><br/>");
      }
      replaceAll(content, "${sessionResults}", tracesHtml.toString());
    }
    
    resp.getWriter().write(content.toString());
    resp.getWriter().flush();
  }
  
  public static void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }
}
