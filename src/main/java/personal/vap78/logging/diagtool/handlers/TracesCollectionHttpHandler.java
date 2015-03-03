package personal.vap78.logging.diagtool.handlers;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.SetLogLevelCommand;

public class TracesCollectionHttpHandler extends AbstractHttpHandler {

  @Override
  public void service(Request req, Response resp) throws Exception {
    super.service(req, resp);
    
    Session session = getSession(req, resp);
    if (session == null) {
      resp.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }
    
    Reader dataReader = req.getReader();
    
    List<String> locations = readToList(dataReader);
    
    SetLogLevelCommand command = new SetLogLevelCommand(session, locations, LogLevel.ALL);
    
    command.executeConsoleTool();
    System.out.println(command.getConsoleOutput());
    //TODO handle errors
  }

  private List<String> readToList(Reader dataReader) throws Exception {
    BufferedReader reader = new BufferedReader(dataReader);
    String line = null;
    List<String> toReturn = new ArrayList<String>();
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (!"".equals(line)) {
        toReturn.add(line.trim());
      }
    }
    return toReturn;
  }
}
