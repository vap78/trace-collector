package personal.vap78.logging.diagtool.http.handlers;

import java.io.File;
import java.util.Map;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.LogFileDescriptor;
import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.TraceCollectionInfo;
import personal.vap78.logging.diagtool.http.Session;
import personal.vap78.logging.diagtool.impl.console.cmd.AbstractLogCommand;
import personal.vap78.logging.diagtool.impl.console.cmd.GetLogsCommand;
import personal.vap78.logging.diagtool.impl.console.cmd.HtmlReportGenerator;
import personal.vap78.logging.diagtool.impl.console.cmd.ListLogFilesCommand;

public class StopTracesCollectionHttpHandler extends AbstractTracesCollectionHandler {

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    Session session = getSession(request, response);
    if (session == null) {
      response.sendError(401);
      return;
    }
    try {
      TraceCollectionInfo info = session.getCurrentTracesCollectionInfo();
      info.setEndTime(System.currentTimeMillis());
  
      setLogLevels(session, request, LogLevel.ERROR);
      
      ListLogFilesCommand listLogFiles = new ListLogFilesCommand(session);
      listLogFiles.executeConsoleTool();
      listLogFiles.printConsoleToSystemOut();
      Map<String, LogFileDescriptor> logFiles = listLogFiles.parseListLogsOutput();
      
      GetLogsCommand getLogs = new GetLogsCommand(session, logFiles.get(AbstractLogCommand.LJS_TRACE).getName());
      getLogs.executeConsoleTool();
      getLogs.printConsoleToSystemOut();
      
      if (!getLogs.isExecutionSuccessful()) {
        response.sendError(500, "Failed to retrieve log files after the trace collector has stopped");
        return;
      }
      
      HtmlReportGenerator reportGenerator = new HtmlReportGenerator(session, logFiles, info.getStartTime(), info.getEndTime());
      File reportFile = reportGenerator.generateHtmlReport();
      response.getWriter().write(reportFile.getName());
      session.addCollectedTraceFile(reportFile.getName());
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(500);
    }
  }

}
