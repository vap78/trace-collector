package personal.vap78.logging.diagtool.impl.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import personal.vap78.logging.diagtool.Session;
import personal.vap78.logging.diagtool.impl.console.cmd.AbstractLogCommand;

public class ConsoleSession extends Session {

  private static Map<String, ConsoleSession> sessions = new HashMap<>();
  
  public static ConsoleSession getById(String id) {
    return sessions.get(id);
  }
  
  public static void deleteSession(String id) {
    sessions.remove(id);
  }

  public ConsoleSession(String id, Properties properties) {
    super(id, properties);
  }


  public static ConsoleSession createSession(String id, Properties props) {
    ConsoleSession session = new ConsoleSession(id, props);
    sessions.put(id, session);
    return session;
  }
  

  public String getHost() {
    return properties.getProperty(AbstractLogCommand.HOST_PARAM);
  }

  public String getAccount() {
    return properties.getProperty(AbstractLogCommand.ACCOUNT_PARAM);
  }

  public String getApplication() {
    return properties.getProperty(AbstractLogCommand.APPLICATION_PARAM);
  }

  public String getUser() {
    return properties.getProperty(AbstractLogCommand.USER_PARAM);
  }

  public String getPassword() {
    return properties.getProperty(AbstractLogCommand.PASSWORD_PARAM);
  }

  public String getSDKPath() {
    return properties.getProperty(AbstractLogCommand.SDK_PATH_PARAM, "");
  }

  public String getProxy() {
    return properties.getProperty(AbstractLogCommand.PROXY_PARAM, "");
  }

  public String getProxyUser() {
    return properties.getProperty(AbstractLogCommand.PROXY_USER_PARAM, "");
  }

  public String getProxyPassword() {
    return properties.getProperty(AbstractLogCommand.PROXY_PASSWORD_PARAM, "");
  }

  public String getLongName() {
    return getHost() + "_" + getAccount() + "_" + getApplication();
  }

  @Override
  public boolean isTrialAccount() {
    return getHost().contains("hanatrial");
  }

}
