package personal.vap78.logging.diagtool.impl.console;

import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.api.StartTracesOperation;
import personal.vap78.logging.diagtool.http.Session;

public class StartTracesConsoleImpl implements StartTracesOperation {
  
  private Session session;

  public StartTracesConsoleImpl(Session session) {
    this.session = session;
  }
  
  @Override
  public void execute(TraceConfiguration configuration) {
    
  }

  @Override
  public boolean isSuccessful() {
    return false;
  }

}
