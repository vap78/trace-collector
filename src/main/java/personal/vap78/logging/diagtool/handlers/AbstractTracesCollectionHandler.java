package personal.vap78.logging.diagtool.handlers;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.SetLogLevelCommand;

public abstract class AbstractTracesCollectionHandler extends AbstractHttpHandler {

  
  protected void setLogLevels(Session session, Request req, LogLevel level) throws Exception {
    
    List<String> locations = readLocationsToList(req);
    
    SetLogLevelCommand command = new SetLogLevelCommand(session, locations, level);
    
    command.executeConsoleTool();
    System.out.println(command.getConsoleOutput());
  }
  
  private List<String> readLocationsToList(Request req) throws Exception {
    BufferedReader reader = new BufferedReader(req.getReader());
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
