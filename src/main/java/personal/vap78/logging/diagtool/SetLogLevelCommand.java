package personal.vap78.logging.diagtool;

public class SetLogLevelCommand extends AbstractLogCommand {
  public static final String SET_LOG_LEVEL_COMMAND = "set-log-level";


  public SetLogLevelCommand(Session session) {
    super(session);
  }

  @Override
  protected void addCommandSpecificParameters() {
    
    String loggers = session.getProperties().getProperty(LOGGERS_PARAM);
    System.out.println("Loggers to be changed: " + loggers);
    String level = session.getProperties().getProperty(LEVEL_PARAM);
    System.out.println("Log level: " + level);

    command.add("--loggers");
    command.add(loggers);
    command.add("--level");
    command.add(level);

  }

  @Override
  protected String getCommandName() {
    return SET_LOG_LEVEL_COMMAND;
  }

}
