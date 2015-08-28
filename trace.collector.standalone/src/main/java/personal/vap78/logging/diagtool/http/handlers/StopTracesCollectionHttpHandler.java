package personal.vap78.logging.diagtool.http.handlers;

import java.io.File;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.HtmlReportGenerator;
import personal.vap78.logging.diagtool.TraceCollectionInfo;
import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.api.StopTracesOperation;
import personal.vap78.logging.diagtool.impl.console.ConsoleOperationsFactory;
import personal.vap78.logging.diagtool.impl.console.ConsoleSession;

public class StopTracesCollectionHttpHandler extends AbstractTracesCollectionHandler {

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    ConsoleSession session = getSession(request, response);
    if (session == null) {
      response.sendError(401);
      return;
    }
    try {
      TraceCollectionInfo info = session.getCurrentTracesCollectionInfo();
      info.setEndTime(System.currentTimeMillis());
  
      StopTracesOperation stopTracesOperation = ConsoleOperationsFactory.getStopTracesOperation(session);
      TraceConfiguration configuration = new TraceConfiguration("temp", readLoggersToList(request));
      stopTracesOperation.execute(configuration);
      if (!stopTracesOperation.isSuccessful()) {
        System.out.println("Failed to stop the trace collector");
        response.sendError(500);
        return;
      }
      
      String reportFile = stopTracesOperation.getReportFileName();
      
      HtmlReportGenerator reportGenerator = new HtmlReportGenerator(session, reportFile, info.getStartTime(), info.getEndTime());
      File reportFileHtml = reportGenerator.generateHtmlReport();
      response.getWriter().write(reportFileHtml.getName());
      session.addCollectedTraceFile(reportFileHtml.getName());
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(500);
    }
  }

}
