package com.sap.cloud.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public abstract class AbstractLogCommand {

  public static final String HOST_PARAM = "host";
  public static final String ACCOUNT_PARAM = "account";
  public static final String APPLICATION_PARAM = "application";
  public static final String USER_PARAM = "user";
  public static final String PASSWORD_PARAM = "password";
  public static final String LEVEL_PARAM = "level";
  public static final String LOGGERS_PARAM = "loggers";
  public static final String SDK_PATH_PARAM = "sdk.path";
  public static final String HTTP_TRACE = "http_trace";
  public static final String LJS_TRACE = "ljs_trace";

  protected StringBuilder consoleOutput;
  protected Properties props;
  protected List<String> command;
  private String executableRoot;
  private String executablePath;
  
  public AbstractLogCommand(Properties props) {
    this.props = props;
    command = new LinkedList<>();
    determinePaths();
  }
  
  public void printCommandParameters() {
    System.out.println("Executing command:");
    for (int i = 0; i < command.size(); i++) {
      String commandPart = command.get(i);
      if ("--password".equals(commandPart)) {
        System.out.print(commandPart);
        System.out.print(" ");
        System.out.print("**************");
        i++;
      } else if (commandPart.contains(" ")) {
        System.out.print("\"" + commandPart + "\"");
      } else {
        System.out.print(commandPart + " ");
      }
    }
    System.out.println();
  }
  
  public BufferedReader executeConsoleTool() throws IOException, CommandExecutionException {
    addCommonCommandParameters();
    
    addCommandSpecificParameters();
    
    ProcessBuilder pb = new ProcessBuilder();
    pb.directory(new File(executableRoot));
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

    return input;
  }

  protected abstract String getCommandName();

  protected abstract void addCommandSpecificParameters();

  public String getConsoleOutput() {
    if (consoleOutput == null) {
      return null;
    }
    return consoleOutput.toString();
  }
  
  protected void addCommonCommandParameters() {
    command.clear();

    String host = props.getProperty(HOST_PARAM);
    String account = props.getProperty(ACCOUNT_PARAM);
    String application = props.getProperty(APPLICATION_PARAM);
    String user = props.getProperty(USER_PARAM);
    String password = props.getProperty(PASSWORD_PARAM);

    System.out.println("Started with following parameters:");
    System.out.println("Landscape host: " + host);
    System.out.println("Account: " + account);
    System.out.println("Appliction: " + application);
    System.out.println("User: " + user);

    command.add("--host");
    command.add(host);
    command.add("--account");
    command.add(account);
    command.add("--application");
    command.add(application);
    command.add("--user");
    command.add(user);
    command.add("--password");
    command.add(password);
  }  
  
  private void determinePaths() {
    String sdkPath = props.getProperty(SDK_PATH_PARAM);

    executableRoot = sdkPath + File.separator + "tools";
    executablePath = executableRoot;
    if (OSDetector.isWindows()) {
      executablePath += File.separator + "neo.bat";
    } else {
      executablePath += File.separator + "neo.sh";
    }
  }
  
  protected void readFullyToSysOut(BufferedReader reader) throws Exception {
    String line = null;
    while ((line = reader.readLine()) != null) {
      System.out.print("[" + this.getClass().getSimpleName() + "]>> ");
      System.out.println(line);
    }
  }

}
