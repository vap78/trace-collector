package personal.vap78.logging.diagtool.http.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.TraceCollectionInfo;
import personal.vap78.logging.diagtool.http.Session;

public class StartTracesCollectionHttpHandler extends AbstractTracesCollectionHandler {

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    
    Session session = getSession(request, response);
    if (session == null) {
      response.sendError(401);
      return;
    }
    
    setLogLevels(session, request, LogLevel.ALL);
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    String logSessionId = format.format(new Date());
    TraceCollectionInfo info = new TraceCollectionInfo();
    info.setStartTime(System.currentTimeMillis());
    info.setId(logSessionId);
    session.setCurrentTracesId(info);
    response.getWriter().write(logSessionId);
    response.getWriter().flush();
  }

}
