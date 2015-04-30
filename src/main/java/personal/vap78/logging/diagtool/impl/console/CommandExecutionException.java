package personal.vap78.logging.diagtool.impl.console;

import personal.vap78.logging.diagtool.impl.console.cmd.AbstractLogCommand;

public class CommandExecutionException extends Exception {

  private static final long serialVersionUID = 1L;
  private AbstractLogCommand command;
  
  public CommandExecutionException(AbstractLogCommand command, String message) {
    super(message);
    this.command = command;
  }
  
  public CommandExecutionException(AbstractLogCommand command, String message, Throwable error) {
    super(message, error);
    this.command = command;
  }
  
  public AbstractLogCommand getCommand() {
    return command;
  }

}
