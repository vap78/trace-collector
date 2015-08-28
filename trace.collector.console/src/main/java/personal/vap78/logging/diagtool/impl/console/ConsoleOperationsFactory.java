package personal.vap78.logging.diagtool.impl.console;

import java.util.Properties;

import personal.vap78.logging.diagtool.api.LoginOperation;
import personal.vap78.logging.diagtool.api.StartTracesOperation;
import personal.vap78.logging.diagtool.api.StopTracesOperation;

public class ConsoleOperationsFactory {

  public static StartTracesOperation getStartTracesOperation(ConsoleSession session) {
    return new StartTracesConsoleImpl(session);
  }
  
  public static StopTracesOperation getStopTracesOperation(ConsoleSession session) {
    return new StopTracesConsoleImpl(session);
  }
  
  public static LoginOperation getLoginOperation(Properties props) {
    return new LoginOperationConsoleImpl(props);
  }
}
