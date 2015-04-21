package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;

public abstract class AbstractLogCommand {

  public static final String HOST_PARAM = "host";
  public static final String ACCOUNT_PARAM = "account";
  public static final String APPLICATION_PARAM = "application";
  public static final String USER_PARAM = "user";
  public static final String PASSWORD_PARAM = "password";
  public static final String LEVEL_PARAM = "level";
  public static final String LOGGERS_PARAM = "loggers";
  public static final String SDK_PATH_PARAM = "sdkPath";
  public static final String HTTP_TRACE = "http_trace";
  public static final String LJS_TRACE = "ljs_trace";
  public static final String PROXY_PARAM = "proxy";
  public static final String PROXY_USER_PARAM = "proxyUser";
  public static final String PROXY_PASSWORD_PARAM = "proxyPassword";

  protected StringBuilder consoleOutput;
  protected Session session;
  protected List<String> command;
  private String executableRoot;
  protected String executablePath;
  
  public AbstractLogCommand(Session session) {
    this.session = session;
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
        System.out.print(" ");
        i++;
      } else if (commandPart.contains(" ")) {
        System.out.print("\"" + commandPart + "\"");
      } else {
        System.out.print(commandPart + " ");
      }
    }
    System.out.println();
  }
  
  public void executeConsoleTool() throws IOException, CommandExecutionException {
    addCommonCommandParameters();
    
    addCommandSpecificParameters();
    
    ProcessBuilder pb = new ProcessBuilder();
    pb.directory(new File("."));
    command.add(0, getCommandName());
    command.add(0, executablePath);

    printCommandParameters();

    pb.command(command);

    if (session.getProxy() != null) {
      Map<String, String> environmentMap = pb.environment();
      setProxy(environmentMap);
    }
    pb.redirectErrorStream(true);
    Process p = pb.start();

    System.out.println("=================================================");
    System.out.println("Executing neo console tool operation " + getCommandName());
    System.out.println("=================================================");
    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
    
    String line = null;
    consoleOutput = new StringBuilder();
    while ((line = input.readLine()) != null) {
      consoleOutput.append(line).append("\n");
    }
  }

  protected void setProxy(Map<String, String> environmentMap) {
    String proxy = session.getProxy();
    if (proxy == null || "".equals(proxy)) {
      System.out.println("No proxy configured for this run");
      return;
    }
    
    if (OSDetector.isWindows()) {
// set HTTP_PROXY_HOST=proxy
// set HTTP_PROXY_PORT=8080
// set HTTPS_PROXY_HOST=proxy
// set HTTPS_PROXY_PORT=8080
// set HTTP_NON_PROXY_HOSTS="localhost"
//
// If you need basic proxy authentication, enter your user name and password:
//
// set HTTP_PROXY_USER=<user name>
// set HTTP_PROXY_PASSWORD=<password>
// set HTTPS_PROXY_USER=<user name>
// set HTTPS_PROXY_PASSWORD=<password>
      String[] hostAndPort = proxy.split(":");
      
      environmentMap.put("HTTP_PROXY_HOST", hostAndPort[0]);
      environmentMap.put("HTTPS_PROXY_HOST", hostAndPort[0]);
      environmentMap.put("HTTP_PROXY_PORT", hostAndPort[1]);
      environmentMap.put("HTTPS_PROXY_PORT", hostAndPort[1]);
      environmentMap.put("HTTP_NON_PROXY_HOSTS", "localhost");
      if (session.getProxyUser() != null && !"".equals(session.getProxyUser())) {
        environmentMap.put("HTTP_PROXY_USER", session.getProxyUser());
        environmentMap.put("HTTP_PROXY_PASSWORD", session.getProxyPassword());
        environmentMap.put("HTTPS_PROXY_USER", session.getProxyUser());
        environmentMap.put("HTTPS_PROXY_PASSWORD", session.getProxyPassword());
      }
    } else {
//      export http_proxy=http://proxy:8080
//      export https_proxy=https://proxy:8080
//      export no_proxy="localhost"
//
//      If you need basic proxy authentication, add your user name and password:
//
//      export http_proxy=http://<user name>:<password>@proxy:8080
//      export https_proxy=https://<user name>:<password>@proxy:8080
      if (session.getProxyUser() != null && !"".equals(session.getProxyUser())) {
        environmentMap.put("http_proxy", "http://" + session.getProxyUser() + ":" + session.getProxyPassword() + "@" + session.getProxy());
        environmentMap.put("https_proxy", "https://" + session.getProxyUser() + ":" + session.getProxyPassword() + "@" + session.getProxy());
      } else {
        environmentMap.put("http_proxy", "http://" + session.getProxy());
        environmentMap.put("https_proxy", "https://" + session.getProxy());
      }
      environmentMap.put("no_proxy", "localhost");
    }
}

protected abstract String getCommandName();

  protected abstract void addCommandSpecificParameters();

  protected abstract boolean isExecutionSuccessful();
  
  public String getConsoleOutput() {
    if (consoleOutput == null) {
      return null;
    }
    return consoleOutput.toString();
  }
  
  protected void addCommonCommandParameters() {
    command.clear();

    String host = session.getHost();
    String account = session.getAccount();
    String application = session.getApplication();
    String user = session.getUser();
    String password = session.getPassword();

    System.out.println("Command " + getCommandName() + " started with following parameters:");
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
    String sdkPath = session.getSDKPath();

    executableRoot = sdkPath + File.separator + "tools";
    executablePath = executableRoot;
    if (OSDetector.isWindows()) {
      executablePath += File.separator + "neo.bat";
    } else {
      executablePath += File.separator + "neo.sh";
    }
  }
  
  public void printConsoleToSystemOut() throws Exception {
    String line = null;
    if (consoleOutput == null) {
      System.out.println("Command output not present");
      return;
    }
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new CharSequenceReader(consoleOutput));
      while ((line = reader.readLine()) != null) {
        System.out.print("[" + this.getClass().getSimpleName() + "]>> ");
        System.out.println(line);
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

}
