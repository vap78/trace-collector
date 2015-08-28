package personal.vap78.logging.diagtool.http.handlers;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;

public abstract class AbstractTracesCollectionHandler extends AbstractHttpHandler {
  
  protected List<String> readLoggersToList(Request req) throws Exception {
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
