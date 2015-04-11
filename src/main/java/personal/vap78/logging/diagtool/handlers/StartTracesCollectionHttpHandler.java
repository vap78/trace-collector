package personal.vap78.logging.diagtool.handlers;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.SetLogLevelCommand;

public class StartTracesCollectionHttpHandler extends AbstractTracesCollectionHandler {

  @Override
  public void service(Request req, Response resp) throws Exception {
    super.service(req, resp);
    
    Session session = getSession(req, resp);
    if (session == null) {
      resp.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }
    
    setLogLevels(session, req, LogLevel.ALL);
    
    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
    String logSessionId = format.format(new Date());
    TraceCollectionInfo info = new TraceCollectionInfo();
    info.setStartTime(System.currentTimeMillis());
    info.setId(logSessionId);
    session.setCurrentTracesId(info);
    resp.getWriter().write(logSessionId);
    resp.getWriter().flush();
  }

}
