package personal.vap78.logging.diagtool.handlers;

import java.io.File;
import java.util.Map;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.AbstractLogCommand;
import personal.vap78.logging.diagtool.GetLogsCommand;
import personal.vap78.logging.diagtool.HtmlReportGenerator;
import personal.vap78.logging.diagtool.ListLogFilesCommand;
import personal.vap78.logging.diagtool.LogFileDescriptor;
import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.Session;

public class StopTracesCollectionHttpHandler extends AbstractTracesCollectionHandler {

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    Session session = getSession(request, response);
    if (session == null) {
      response.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }
    
    TraceCollectionInfo info = session.getCurrentTracesCollectionInfo();
    info.setEndTime(System.currentTimeMillis());

    setLogLevels(session, request, LogLevel.ERROR);
    
    ListLogFilesCommand listLogFiles = new ListLogFilesCommand(session);
    listLogFiles.executeConsoleTool();
    Map<String, LogFileDescriptor> logFiles = listLogFiles.parseListLogsOutput();
    
    GetLogsCommand getLogs = new GetLogsCommand(session, logFiles.get(AbstractLogCommand.LJS_TRACE).getName());
    getLogs.executeConsoleTool();
    
    if (!getLogs.isExecutionSuccessful()) {
      response.sendError(500, "Failed to retrieve log files after the trace collector has stopped");
      return;
    }
    
    HtmlReportGenerator reportGenerator = new HtmlReportGenerator(null, logFiles, info.getStartTime(), info.getEndTime());
    File reportFile = reportGenerator.generateHtmlReport();
    response.getWriter().write(reportFile.getName());
  }

}
