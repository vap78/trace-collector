package personal.vap78.logging.diagtool.impl.console;

import java.security.SecureRandom;
import java.util.Properties;
import java.util.UUID;

import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.api.LoginOperation;
import personal.vap78.logging.diagtool.impl.console.cmd.ListLogFilesCommand;

public class LoginOperationConsoleImpl implements LoginOperation {

  private ConsoleSession session;
  
  private boolean isSuccessful = false;
  private Properties props;

  public LoginOperationConsoleImpl(Properties props) {
    this.props = props;
  }

  @Override
  public void execute(TraceConfiguration configuration) {
    SecureRandom random = new SecureRandom();
    UUID uuid = new UUID(random.nextLong(), random.nextLong());

    session = ConsoleSession.createSession(uuid.toString(), props);
    
    ListLogFilesCommand command = new ListLogFilesCommand(session);
    try {
      command.executeConsoleTool();
      command.printConsoleToSystemOut();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    
    isSuccessful = command.isExecutionSuccessful();
    
    if (!isSuccessful) {
      ConsoleSession.deleteSession(session.getId());
      session = null;
    }
  }

  @Override
  public boolean isSuccessful() {
    return isSuccessful;
  }

  @Override
  public ConsoleSession getSession() {
    return session;
  }

}
