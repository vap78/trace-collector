package personal.vap78.logging.diagtool;

public class CommandExecutionException extends Exception {

  private static final long serialVersionUID = 1L;
  private AbstractLogCommand command;
  
  public CommandExecutionException(AbstractLogCommand command, String message) {
    super(message);
    this.command = command;
  }
  
  CommandExecutionException(AbstractLogCommand command, String message, Throwable error) {
    super(message, error);
    this.command = command;
  }
  
  public AbstractLogCommand getCommand() {
    return command;
  }

}
