package personal.vap78.logging.diagtool.impl.console;

import java.util.Map;

import personal.vap78.logging.diagtool.LogFileDescriptor;
import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.api.StopTracesOperation;
import personal.vap78.logging.diagtool.http.Session;
import personal.vap78.logging.diagtool.impl.console.cmd.AbstractLogCommand;
import personal.vap78.logging.diagtool.impl.console.cmd.GetLogsCommand;
import personal.vap78.logging.diagtool.impl.console.cmd.ListLogFilesCommand;

public class StopTracesConsoleImpl extends BaseStartStopOperation implements StopTracesOperation {
  
  private Session session;
  private boolean successful;
  private String reportFileName;
  
  public StopTracesConsoleImpl(Session session) {
    this.session = session;
  }

  @Override
  public void execute(TraceConfiguration configuration) {
    try {
      setLoggerLevel(session, configuration, LogLevel.ERROR);
      
      ListLogFilesCommand listLogFiles = new ListLogFilesCommand(session);
      listLogFiles.executeConsoleTool();
      listLogFiles.printConsoleToSystemOut();
      if (!listLogFiles.isExecutionSuccessful()) {
        throw new CommandExecutionException(listLogFiles, "Failed to list the log files");
      }
      Map<String, LogFileDescriptor> logFiles = listLogFiles.parseListLogsOutput();
      String logFileToDownload =  logFiles.get(AbstractLogCommand.LJS_TRACE).getName();
      GetLogsCommand getLogs = new GetLogsCommand(session, logFileToDownload);
      getLogs.executeConsoleTool();
      getLogs.printConsoleToSystemOut();
      
      if (getLogs.isExecutionSuccessful()) {
        reportFileName = logFileToDownload;
        successful = true;
      } else {
        throw new CommandExecutionException(listLogFiles, "Failed to download the log file");
      }
    } catch (CommandExecutionException e) {
      e.printStackTrace();
      successful = false;
    }
    
  }

  @Override
  public boolean isSuccessful() {
    return successful;
  }

  @Override
  public String getReportFileName() {
    return reportFileName;
  }

}
