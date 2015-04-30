package personal.vap78.logging.diagtool.impl.console;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.api.StartTracesOperation;
import personal.vap78.logging.diagtool.http.Session;

public class StartTracesConsoleImpl extends BaseStartStopOperation implements StartTracesOperation {
  private Session session;

  private boolean successful;
  
  public StartTracesConsoleImpl(Session session) {
    this.session = session;
  }
  
  
  @Override
  public void execute(TraceConfiguration configuration) {
    try {
      setLoggerLevel(session, configuration, LogLevel.ALL);
    } catch (CommandExecutionException e) {
      e.printStackTrace();
      successful = false;
    }
  }

  @Override
  public boolean isSuccessful() {
    return successful;
  }

}
