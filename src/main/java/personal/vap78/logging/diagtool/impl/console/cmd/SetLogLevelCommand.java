package personal.vap78.logging.diagtool.impl.console.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import personal.vap78.logging.diagtool.LogLevel;
import personal.vap78.logging.diagtool.http.Session;
import personal.vap78.logging.diagtool.impl.console.CommandExecutionException;

public class SetLogLevelCommand extends AbstractLogCommand {
  public static final String SET_LOG_LEVEL_COMMAND = "set-log-level";
  private List<String> loggersToConfigure;
  private LogLevel level;

  public SetLogLevelCommand(Session session, List<String> loggers, LogLevel level) {
    super(session);
    this.loggersToConfigure = loggers;
    this.level = level;
  }

  @Override
  protected void addCommandSpecificParameters() {
    
//    String loggers = loggersToString();
//    System.out.println("Loggers to be changed: " + loggers);
//    String levelStr = level.getName();
//    System.out.println("Log level: " + levelStr);

//    command.add("--loggers");
//    command.add(loggers);
//    command.add("--level");
//    command.add(levelStr);
  }
  
  @Override
  protected String getCommandName() {
    return SET_LOG_LEVEL_COMMAND;
  }
  
  @Override
  public void executeConsoleTool() throws CommandExecutionException {
    System.out.println("Setting trace level for " + loggersToConfigure.size() + " loggers");
    consoleOutput = new StringBuilder();
    
    File propsFile = new File(session.getId() + ".properties"); 
    Properties props = new Properties();
    props.put(AbstractLogCommand.ACCOUNT_PARAM, session.getAccount());
    props.put(AbstractLogCommand.HOST_PARAM, session.getHost());
    props.put(AbstractLogCommand.APPLICATION_PARAM, session.getApplication());
    props.put(AbstractLogCommand.USER_PARAM, session.getUser());
    props.put(AbstractLogCommand.LOGGERS_PARAM, loggersToString());
    props.put(AbstractLogCommand.LEVEL_PARAM, level.getName());
    try {
      props.store(new FileOutputStream(propsFile), "");
      
      command.add(propsFile.getName());
      command.add("--password");
      command.add(session.getPassword());
      
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
    } catch (IOException e) {
      throw new CommandExecutionException(null, "", e);
    }
  }
  
  @Override
  public boolean isExecutionSuccessful() {
    return consoleOutput != null && consoleOutput.indexOf("[set-log-level] operation is successful.") != -1;
  }
  
  private String loggersToString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < loggersToConfigure.size(); i++) {
      builder.append(loggersToConfigure.get(i));
      if (i < loggersToConfigure.size() - 1) {
        builder.append(",");
      }
    }
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
