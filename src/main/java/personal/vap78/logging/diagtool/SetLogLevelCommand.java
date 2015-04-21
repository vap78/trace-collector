package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class SetLogLevelCommand extends AbstractLogCommand {
  private static final int MAX_LOGGERS_COUNT = 50;
  public static final String SET_LOG_LEVEL_COMMAND = "set-log-level";
  private List<String> loggersToConfigure;
  private LogLevel level;
  private int loggersPart;
  private boolean finishedAllLoggers = false;


  public SetLogLevelCommand(Session session, List<String> loggers, LogLevel level) {
    super(session);
    this.loggersToConfigure = loggers;
    this.level = level;
    loggersPart = 0;
  }

  @Override
  protected void addCommandSpecificParameters() {
    
    String loggers = loggersToString();
    System.out.println("Loggers to be changed: " + loggers);
    String levelStr = level.getName();
    System.out.println("Log level: " + levelStr);

    command.add("--loggers");
    command.add(loggers);
    command.add("--level");
    command.add(levelStr);
  }
  
  @Override
  protected String getCommandName() {
    return SET_LOG_LEVEL_COMMAND;
  }
  
  @Override
  public void executeConsoleTool() throws IOException, CommandExecutionException {
    System.out.println("Setting trace level for " + loggersToConfigure.size() + " loggers");
    consoleOutput = new StringBuilder();
    do {
      addCommonCommandParameters();
      
      addCommandSpecificParameters();
      
      ProcessBuilder pb = new ProcessBuilder();

      if (session.getProxy() != null) {
        Map<String, String> environmentMap = pb.environment();
        setProxy(environmentMap);
      }

      pb.directory(new File("."));
      command.add(0, getCommandName());
      command.add(0, executablePath);
  
      printCommandParameters();
  
      pb.command(command);
      pb.redirectErrorStream(true);
      Process p = pb.start();
  
      System.out.println("=================================================");
      System.out.println("Executing neo console tool operation " + getCommandName());
      System.out.println("=================================================");
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      
      String line = null;
      while ((line = input.readLine()) != null) {
        consoleOutput.append(line).append("\n");
      }
    } while (finishedAllLoggers == false);
  }
  
  @Override
  public boolean isExecutionSuccessful() {
    return consoleOutput != null && consoleOutput.indexOf("[set-log-level] operation is successful.") != -1;
  }
  
  private String loggersToString() {
    if (finishedAllLoggers) {
      throw new RuntimeException("Already iterated all loggers");
    }
    StringBuilder builder = new StringBuilder();
    for (int i = loggersPart * MAX_LOGGERS_COUNT; i < loggersPart * MAX_LOGGERS_COUNT + MAX_LOGGERS_COUNT; i++) {
      if (i < loggersToConfigure.size()) {
        builder.append(loggersToConfigure.get(i));
        if (i < loggersPart * MAX_LOGGERS_COUNT + MAX_LOGGERS_COUNT - 1 && i < loggersToConfigure.size() - 1) {
          builder.append(",");
        }
      } else {
        finishedAllLoggers = true;
        break;
      }
    }
    loggersPart++;
    return builder.toString();
  }
  
//  public static void main(String[] args) {
//    List<String> locations = new ArrayList<>();
//    for (int i = 0; i < 5; i++) {
//      locations.add("location" + i);
//    }
//    SetLogLevelCommand cmd = new SetLogLevelCommand(new Session("", new Properties()), locations,LogLevel.ALL);
//    System.out.println(cmd.loggersToString());
//    System.out.println(cmd.loggersToString());
//    System.out.println(cmd.loggersToString());
//    System.out.println(cmd.loggersToString());
//  }
  
}
